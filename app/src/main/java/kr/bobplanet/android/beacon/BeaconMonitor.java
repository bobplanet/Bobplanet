package kr.bobplanet.android.beacon;

import android.app.AlarmManager;
import android.content.Context;
import android.os.Build;
import android.os.RemoteException;
import android.util.Log;

import com.google.common.collect.Lists;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor;

import java.net.MalformedURLException;

import hugo.weaving.DebugLog;
import kr.bobplanet.android.NotifyManager;
import kr.bobplanet.android.R;
import kr.bobplanet.android.log.UserActionLog;

/**
 * 밥플래닛 Beacon을 모니터링하는 객체.
 *
 * - BLE(Bluetooth Low Energy)는 API 레벨 18(JELLY_BEAN_MR2)부터 지원하므로 그 이하의 기기는 그냥 pass
 * - 식당을 떠나자 마자 알림메시지를 띄운다.
 *
 * @author heonkyu.jin
 * @version 15. 11. 15
 */
public class BeaconMonitor implements BootstrapNotifier {
    private static final String TAG = BeaconMonitor.class.getSimpleName();

    /**
     * Eddystone 패킷의 UUID 식별자
     */
    private static final int EDDY_SERVICE_UUID = 0xfeaa;

    /**
     * Eddystone 패킷의 typecode
     */
    private static final int EDDY_URL_TYPECODE = 0x10;

    /**
     * Eddystone URL 패킷의 식별자
     */
    private static final String EDDY_URL_FRAME = "s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-20v";

    /**
     * Eddystone UID 패킷의 식별자. 여기서는 이용하지 않음
     */
    @SuppressWarnings("unused")
    private static final String EDDY_UID_FRAME = "s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19";

    /**
     * Eddystone TLM 패킷의 식별자. 여기서는 이용하지 않음
     */
    @SuppressWarnings("unused")
    private static final String EDDY_TLM_FRAME = "x,s:0-1=feaa,m:2-2=20,d:3-3,d:4-5,d:6-7,d:8-11,d:12-15";

    /**
     * 밥플래닛 비콘의 ID. 이 클래스 내부에서만 사용하는 값이므로 아무렇게나 지정해도 무방.
     */
    private static final String REGION_ID_BOBPLANET = "bobplanet";

    /**
     * 밥플래닛 비콘에서 송출하는 URL. 비콘에 지정된 값이므로 변경불가.
     */
    private static final String BEACON_URL = "http://bobplanet.kr";

    /**
     * 밥플래닛 비콘의 식별자.
     */
    private static Identifier BEACON_IDENTIFIER;

    private Context applicationContext;
    private BeaconManager beaconManager;

    private BeaconCriteria.RegionEntrance lastEntrance;

    static {
        try {
            byte[] beaconUrlBytes = UrlBeaconUrlCompressor.compress(BEACON_URL);
            BEACON_IDENTIFIER = Identifier.fromBytes(beaconUrlBytes, 0, beaconUrlBytes.length, false);
        } catch (MalformedURLException e) {
            Log.d(TAG, "BeaconMonitor()", e);
        }
    }

    public BeaconMonitor(Context context) {
        this.applicationContext = context;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) return;

        beaconManager = BeaconManager.getInstanceForApplication(applicationContext);
        BeaconParser eddyParser = new BeaconParser().setBeaconLayout(EDDY_URL_FRAME);
        beaconManager.getBeaconParsers().add(eddyParser);

        // 비콘 스캔을 시작
        new RegionBootstrap(this, new Region(REGION_ID_BOBPLANET, Lists.newArrayList(BEACON_IDENTIFIER)));
    }

    @Override
    public Context getApplicationContext() {
        return applicationContext;
    }

    /**
     * 식사시간이냐 아니냐에 따라 비콘 스캔주기를 조정
     */
    private void updateScanInterval() {
        try {
            beaconManager.setBackgroundBetweenScanPeriod(BeaconCriteria.getScanInterval());
            beaconManager.updateScanPeriods();
        } catch (RemoteException e) {
            Log.w(TAG, "updateScanInterval()", e);
        }
    }

    /**
     * 밥플래닛 비콘이 한개라도 detect되었을 때(=밥플래닛 region에 진입했을 때) 호출됨.
     *
     * @param region
     */
    @Override
    @DebugLog
    public void didEnterRegion(Region region) {
        Log.i(TAG, "Entered region");
        UserActionLog.regionEnter(region.getBluetoothAddress());

        updateScanInterval();

        lastEntrance = BeaconCriteria.regionEntered();

        beaconManager.setRangeNotifier((beacons, r) -> {
            Log.v(TAG, "RangeNotifier: id = " + r.getId1());

            if (!r.getId1().equals(BEACON_IDENTIFIER)) return;

            lastEntrance.markStillResiding();

            for (Beacon beacon : beacons) {
                if (beacon.getServiceUuid() == EDDY_SERVICE_UUID &&
                        beacon.getBeaconTypeCode() == EDDY_URL_TYPECODE) {
                    UserActionLog.beaconSeen(beacon.getBluetoothAddress(), beacon.getDistance());
                }
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            Log.w(TAG, "startRangingBeaconsInRegion()", e);
        }
    }

    /**
     * 비콘에서 멀어진 때 호출되는 콜백.
     * 식당에서 밥을 먹은 것으로 판단될 때만 알림메시지를 등록한다.
     *
     * @param region
     */
    @Override
    @DebugLog
    public void didExitRegion(Region region) {
        Log.i(TAG, "Exited region");
        UserActionLog.regionLeave(region.getBluetoothAddress());

        updateScanInterval();

        try {
            beaconManager.stopRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            Log.w(TAG, "didExitRegion()", e);
        }

        if (lastEntrance.isForDining()) {
            NotifyManager notifyManager = new NotifyManager(applicationContext);
            notifyManager.registerNotification(
                    applicationContext.getString(R.string.noti_beacon_title),
                    applicationContext.getString(R.string.noti_beacon_text),
                    null, null
            );
        }
    }

    @Override
    @DebugLog
    public void didDetermineStateForRegion(int state, Region region) {
    }
}
