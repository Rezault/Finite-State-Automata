import java.util.*;

public class NFA extends AbstractFSA {
    @Override
    public DFA toDFA() {
        // check if we have a start, if not then throw an exception
        if (start == null) {
            throw new IllegalStateException("This automaton has no start state");
        }

        DFA dfa = new DFA();

        // DFA start state will be the epsilon closure of NFA's start state
        // i.e. if NFA starts at 0 and reaches {1,2,3} after epsilon closure, then
        // DFA's start state represents the whole set {0,1,2,3}
        Set<State> startStates = epsilonClosure(Set.of(start));

        // we want to map all of NFA possible states to 1 DFA state
        // i.e. {0,1,2,3} -> 0 (Set<State> -> State)
        // if ANY of these states are accepting, then the corresponding DFA state will
        // also be accepting
        Map<Set<State>, State> nfaToDfaStates = new HashMap<>();

        // create new starting state for dfa, set to accepting if ANY of the start
        // states are accepting
        State dfaStart = dfa.addState(true, anyAccept(startStates));
        nfaToDfaStates.put(hashSetStates(startStates), dfaStart);

        // now we need to convert the rest of the states
        Deque<Set<State>> toConvert = new ArrayDeque<>();
        toConvert.add(startStates);

        while (!toConvert.isEmpty()) {
            // take the first set of states and get the corresponding dfa state
            // we will later add a transition to this dfa state
            Set<State> curr = toConvert.removeFirst();
            State dfaState = nfaToDfaStates.get(hashSetStates(curr));

            // loop through the alphabet of this automaton
            // for every letter, we want to get the next possible states for a given set of
            // states
            for (char x : alphabet) {
                Set<State> nextStates = epsilonClosure(computeSetOnSymbol(curr, x));
                if (nextStates.isEmpty())
                    continue; // no states means we can just move to the next character

                // get the hashkey for the next states and check if it exists in nfaToDfaStates
                Set<State> key = hashSetStates(nextStates);
                State newDfaState = nfaToDfaStates.get(key);
                if (newDfaState == null) {
                    // doesn't exist, this means we need to create a new dfa state
                    newDfaState = dfa.addState(false, anyAccept(nextStates));
                    nfaToDfaStates.put(key, newDfaState);
                    toConvert.addLast(nextStates);
                }
                // add a transition from the current state to the new one
                dfa.addTransition(dfaState, x, newDfaState);
            }
        }

        return dfa;
    }

    private boolean anyAccept(Set<State> states) {
        // check if any state in a set of states is accepting
        // since NFA needs atleast 1 state to be accepting, we will use this to
        // determine
        // if a state in the DFA is accepting or not
        for (State s : states) {
            if (s.isAccept())
                return true;
        }
        return false;
    }

    private Set<State> hashSetStates(Set<State> s) {
        // since sets dont preserve order, we need to create a way to store them
        // properly
        // {q1, q2, q3} might not necessarily equal {q3, q2, q1}, so looking it up in
        // a map might not work
        // treesets sort elements in ascending order, so we will use one
        // this ensures we get the same key every time

        Set<State> hashed = new TreeSet<>(Comparator.comparing(State::id));
        hashed.addAll(s);
        return hashed;
    }

    private Set<State> computeSetOnSymbol(Set<State> states, char x) {
        // go through each state in the set and add the next states
        // on the given character to a new set
        Set<State> returnStates = new LinkedHashSet<>();
        for (State s : states) {
            // return next states from current state
            returnStates.addAll(next(s, x));
        }
        return returnStates;
    }
}
