public class State {
    private int id;
    private boolean start;
    private boolean accept;

    public State(int id, boolean start, boolean accept) {
        this.id = id;
        this.start = start;
        this.accept = accept;
    }

    public int id() {
        return id;
    }

    public boolean isStart() {
        return start;
    }

    public boolean isAccept() {
        return accept;
    }
}
