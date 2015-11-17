package kr.bobplanet.android;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor;

import java.util.Collection;

import hugo.weaving.DebugLog;
import kr.bobplanet.android.log.UserActionLog;

/**
 *
 * @author heonkyu.jin
 * @version 15. 11. 15
 */
public class BeaconDetector implements BootstrapNotifier, RangeNotifier {
    private static final String TAG = BeaconDetector.class.getSimpleName();

    private static final int EDDY_SERVICE_UUID = 0xfeaa;
    private static final int EDDY_URL_TYPECODE = 0x10;

    private static final String EDDY_UID_FRAME = "s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19";
    private static final String EDDY_TLM_FRAME = "x,s:0-1=feaa,m:2-2=20,d:3-3,d:4-5,d:6-7,d:8-11,d:12-15";
    private static final String EDDY_URL_FRAME = "s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-20v";

    private static final String REGION_NAME_SKP = "skplanet-10f";

    private Context applicationContext;
    private BeaconManager beaconManager;
    private RegionBootstrap regionBootstrap;

    protected BeaconDetector(Context context) {
        this.applicationContext = context;
        beaconManager = BeaconManager.getInstanceForApplication(applicationContext);

        BeaconParser eddyParser = new BeaconParser().setBeaconLayout(EDDY_URL_FRAME);
        beaconManager.getBeaconParsers().add(eddyParser);

        Region region = new Region(REGION_NAME_SKP, null, null, null);
        regionBootstrap = new RegionBootstrap(this, region);
    }

    @Override
    public Context getApplicationContext() {
        return applicationContext;
    }

    @Override
    @DebugLog
    public void didEnterRegion(Region region) {
        Log.i(TAG, "Entered region");

        try {
            beaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            Log.w(TAG, "startRangingBeaconsInRegion()", e);
        }

        beaconManager.setRangeNotifier(this);
    }

    @Override
    @DebugLog
    public void didExitRegion(Region region) {
        Log.i(TAG, "Exited region");
    }

    @Override
    @DebugLog
    public void didDetermineStateForRegion(int i, Region region) {

    }

    @Override
    @DebugLog
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        NotifyManager notifyManager = new NotifyManager(applicationContext);

        for (Beacon beacon : beacons) {
            if (beacon.getServiceUuid() == EDDY_SERVICE_UUID &&
                    beacon.getBeaconTypeCode() == EDDY_URL_TYPECODE) {
                String url = UrlBeaconUrlCompressor.uncompress(beacon.getId1().toByteArray());
                Log.i(TAG, "URL =" + url);
                UserActionLog.beaconSeen(beacon.getBluetoothAddress(), beacon.getDistance());
                regionBootstrap.disable();

                notifyManager.registerNotification("Beacon!", url, null, null);
            }
        }
    }
}
