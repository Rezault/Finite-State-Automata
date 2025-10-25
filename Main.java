public class Main {
    public static void main(String[] args) {
        NFA nfa = new NFA();
        State s0 = nfa.addState(true, false);
        State s1 = nfa.addState(false, false);
        State s2 = nfa.addState(false, false);
        State s3 = nfa.addState(false, false);
        State s4 = nfa.addState(false, false);
        State s5 = nfa.addState(false, false);
        State s6 = nfa.addState(false, false);
        State s7 = nfa.addState(false, false);
        State s8 = nfa.addState(false, false);
        State s9 = nfa.addState(false, false);
        State s10 = nfa.addState(false, true);

        nfa.addTransition(s0, null, s1);
        nfa.addTransition(s0, null, s7);
        nfa.addTransition(s1, null, s2);
        nfa.addTransition(s1, null, s4);
        nfa.addTransition(s2, 'a', s3);
        nfa.addTransition(s4, 'b', s5);
        nfa.addTransition(s3, null, s6);
        nfa.addTransition(s5, null, s6);
        nfa.addTransition(s6, null, s1);
        nfa.addTransition(s6, null, s7);
        nfa.addTransition(s7, 'a', s8);
        nfa.addTransition(s8, 'b', s9);
        nfa.addTransition(s9, 'b', s10);

        String[] tests = { "abb", "aabb", "babb", "ababb", "ab", "aba", "bbbabb", "", "cabb" };
        for (String t : tests) {
            System.out.printf("NFA accepts %-8s -> %s%n", t, nfa.accepts(t));
        }

        DFA dfa = nfa.toDFA();
        System.out.println("Is DFA deterministic? " + dfa.deterministic());
        for (String t : tests) {
            System.out.printf("DFA accepts %-8s -> %s%n", t, dfa.accepts(t));
        }
    }
}
