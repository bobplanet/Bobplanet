package kr.bobplanet.backend.model;

/**
 * 사용자가 남긴 코멘트.
 *
 * @author hkjinlee
 * @version 15. 11. 14..
 */
public class Comment {
    /**
     * 메뉴명.
     */
    private String text;

    /**
     * 이 코멘트를 남긴 사람 수.
     */
    private int count;

    public Comment() {}

    public Comment(String text) {
        this.text = text;
        this.count = 1;
    }

    public String getText() {
        return text;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void increaseCount() {
        count++;
    }

    public void decreaseCount() {
        count--;
    }

}
