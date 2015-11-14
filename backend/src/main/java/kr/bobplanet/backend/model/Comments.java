package kr.bobplanet.backend.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 메뉴에 대한 사용자 코멘트 리스트 객체.
 * 사용자가 평가를 수정하면서 과거 코멘트를 삭제하거나 새로운 코멘트를 추가할 수 있으므로,
 * 코멘트에 대한 추가/삭제 기능을 담당한다.
 * 
 * @author heonkyu.jin
 * @version 15. 11. 7.
 */
public class Comments {
    private List<Comment> comments = new ArrayList<>();

    public Comments() {
    }

    public Comment getComment(int position) {
        return comments.get(position);
    }

    public List<Comment> getComments() {
        return comments;
    }

    public int getSize() {
        return comments.size();
    }

    public void add(String text) {
        for (Comment entry : comments) {
            if (entry.getText().equals(text)) {
                entry.increaseCount();
                return;
            }
        }
        comments.add(new Comment(text));
        return;
    }

    public void add(List<String> texts) {
        for (String text : texts) {
            add(text);
        }
    }

    public void remove(String text) {
        for (Iterator<Comment> it = comments.iterator(); it.hasNext(); ) {
            Comment entry = it.next();
            if (entry.getText().equals(text)) {
                entry.decreaseCount();
                if (entry.getCount() == 0) {
                    it.remove();
                }
            }
        }
    }

    public void remove(List<String> texts) {
        for (String text : texts) {
            remove(text);
        }
    }

}
