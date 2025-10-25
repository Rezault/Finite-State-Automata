import java.util.Set;

public interface Automaton {
    // add new state to automaton
    State addState(boolean start, boolean accept);

    // add transition between two states
    void addTransition(State s1, Character symbol, State s2);

    // check if automaton accepts string s
    boolean accepts(String s);

    // return Set of states from current state following an empty transition
    Set<State> closure(State s);

    // return Set of states from current state after a specific transition
    Set<State> next(State s, Character symbol);

    // check if DFA
    boolean deterministic();

    // convert to DFA
    DFA toDFA();
}