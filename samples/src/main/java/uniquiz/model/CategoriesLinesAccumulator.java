package uniquiz.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fazzou on 04.03.16.
 */
public class CategoriesLinesAccumulator {
    private boolean closed = false;
    private List<String> lines = new ArrayList<>();

    public boolean isClosed() {
        return closed;
    }

    public void close() {
        this.closed = true;
    }

    public void push(String line) {
        lines.add(line);
    }

    public List<String> getLines() {
        return lines;
    }

    @Override
    public String toString() {
        return "CategoriesLinesAccumulator{" +
                "lines=" + lines +
                '}';
    }
}
