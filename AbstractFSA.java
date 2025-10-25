import java.util.*;

abstract class AbstractFSA implements Automaton {
    // (Q, S, q0, A, d)
    protected Set<State> states = new LinkedHashSet<>();
    protected Set<Character> alphabet = new LinkedHashSet<>();
    protected Set<State> accepting = new LinkedHashSet<>();
    protected State start = null;
    protected Map<State, Map<Character, Set<State>>> delta = new HashMap<>(); // transition function

    // increment this every time we add a state to give it a new id
    private int nextId = 0;

    @Override
    public State addState(boolean startingState, boolean acceptingState) {
        State newState = new State(nextId++, startingState, acceptingState);
        states.add(newState);

        // if this is a starting state, make it so
        if (startingState) {
            // check that we don't have a start state already
            if (start != null) {
                throw new IllegalStateException("Automaton already has a start state");
            }
            start = newState;
        }

        // add to accepting states if it is an accepting state
        if (acceptingState) {
            accepting.add(newState);
        }

        // create a new transition function for this new state, leave empty for now
        delta.put(newState, new HashMap<>());

        return newState;
    }

    @Override
    public void addTransition(State s1, Character symbol, State s2) {
        // check if states are valid
        if (!states.contains(s1) || !states.contains(s2)) {
            throw new IllegalArgumentException("These states don't exist in the automaton");
        }

        // check that the symbol is not null (ignore epsilon)
        if (symbol != null) {
            // add to the alphabet
            alphabet.add(symbol);
        }

        // get transition functions for s1, then add a transition to s2 for the symbol
        // if set for symbol doesnt exist, then create it. computeIfAbsent is useful
        // here
        delta.get(s1)
                .computeIfAbsent(symbol, _ -> new LinkedHashSet<>())
                .add(s2);

        // System.out.println(delta);
    }

    // epsilon-closure: this must be applied before reading input and after
    // apply to all next states too
    public Set<State> epsilonClosure(Set<State> begin) {
        // need to perform a DFS until we consume all epsilon transitions.
        // use stack
        Deque<State> stack = new ArrayDeque<>(begin);
        Set<State> allStates = new LinkedHashSet<>(begin);

        while (!stack.isEmpty()) {
            // pop the stack for the next state and find all the next possible states
            State s = stack.pop();

            // loop through all possible states
            for (State q : closure(s)) {
                // only add this node to the stack if we've never seen it before
                // if we have seen it, just ignore it
                if (allStates.add(q)) {
                    stack.push(q);
                }
            }
        }

        return allStates;
    }

    @Override
    public boolean accepts(String s) {
        // check if we have a starting state
        if (start == null) {
            return false;
        }

        // since an FSA considers all possible states, we need to store all of them
        // new linked hash set and append the epsilon closure of start
        Set<State> currStates = epsilonClosure(Set.of(start));

        // loop through the symbols in the string
        for (char symbol : s.toCharArray()) {
            // check if the symbol is in the alphabet. if not, just return false
            if (!alphabet.contains(symbol)) {
                return false;
            }

            // new set to store all possible next states
            Set<State> nextStates = new LinkedHashSet<>();

            // loop through all states
            for (State q : currStates) {
                // use the next function to get the possible states
                Set<State> possibleStates = next(q, symbol);

                if (possibleStates != null) {
                    nextStates.addAll(possibleStates);
                }
            }
            // perform epsilon closure on next states
            currStates = epsilonClosure(nextStates);
        }

        // no epsilon-closure needed, nextStates is already closed
        // check all of the current states and see if any of them is accepting
        return currStates.stream().anyMatch(accepting::contains);
    }

    @Override
    public Set<State> closure(State s) {
        // an empty transition is just null (epsilon)
        // just use the next function and pass null as the symbol
        return next(s, null);
    }

    @Override
    public Set<State> next(State s, Character symbol) {
        Map<Character, Set<State>> states = delta.get(s);
        if (states == null)
            return Collections.emptySet(); // found no entry for this state
        return states.getOrDefault(symbol, Collections.emptySet()); // get the possible states or return an empty set
    };

    @Override
    public boolean deterministic() {
        // loop through all states and check their transition function
        // if all of the transition functions return 1 state, it is deterministic.
        for (State s : states) {
            Map<Character, Set<State>> nextStates = delta.get(s);

            // loop through every key available
            for (Character c : nextStates.keySet()) {
                // we can have no epsilon transitions in a DFA, so if c is null return false
                // if the size of the next states set is greater than 1, return false
                if (c == null | nextStates.get(c).size() > 1) {
                    return false;
                }
            }
        }

        return true;
    }
}
