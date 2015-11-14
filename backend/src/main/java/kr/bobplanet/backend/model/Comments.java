package kr.bobplanet.backend.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 사용자들이 메뉴에 매긴 comment
 * 
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
