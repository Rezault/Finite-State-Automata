public class DFA extends AbstractFSA {
    @Override
    public void addTransition(State s1, Character symbol, State s2) {
        // check if symbol is not null. dfa cannot have epsilon transitions
        if (symbol == null) {
            throw new IllegalArgumentException("DFA cannot have epsilon transitions");
        }
        // just call the super constructor for adding a transition
        super.addTransition(s1, symbol, s2);
    }

    @Override
    public DFA toDFA() {
        return this;
    }

    @Override
    public boolean deterministic() {
        return true;
    }
}
