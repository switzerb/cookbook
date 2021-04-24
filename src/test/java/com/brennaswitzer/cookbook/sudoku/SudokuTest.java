package com.brennaswitzer.cookbook.sudoku;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class SudokuTest {

    protected abstract Solver getSolver(String puzzle);

    private void check(String puzzle, String solution) {
        int dim = (int) Math.sqrt(puzzle.length());
        assertEquals(dim * dim, puzzle.length());
        assertEquals(puzzle.length(), solution.length());
        for (int i = 0, l = puzzle.length(); i < l; i++) {
            char c = puzzle.charAt(i);
            if (c == '.') continue;
            assertEquals(c + " in position " + i + " is inconsistent", "" + solution.charAt(i), "" + c);
        }
    }

    private void solve(String puzzle, String solution) {
        if (solution != null) check(puzzle, solution);
        Solver solver = getSolver(puzzle);
        String solved = solver.getSolution();
        System.out.printf("puzzle  : %s%nsolved  : %s%nsolution: %s%nframes  : %,d%nelapsed : %,d μs%n", puzzle, solved, solution, solver.getFrameCount(), solver.getElapsed() / 1000);
        assertTrue(solved, solver.isSolved());
        if (solution != null) {
            check(solved, solution);
            assertEquals(solution, solved);
        }
    }

    @Test
    public void _4x4() {
        solve("4213132.24313142",
                "4213132424313142");
    }

    @Test
    public void blank() {
        // linear: 123456789456789123789123456214365897365897214897214365531642978642978531978531642
        solve(".................................................................................",
                null);
    }

    @Test
    public void oneStar() {
        solve(".549..67.8..5...4...68243...4...5..2.32...19.7..3...8...54139...2...9..3.19..786.",
                "254931678893576241176824359948165732532748196761392485685413927427689513319257864");
    }

    @Test
    public void fourStar() {
        solve(".4......8.9...451...2...6..37...2..1...3.7...2..9...53..9...8...836...4.5......2.",
                "145276938697834512832159674378562491951347286264981753429713865783625149516498327");
    }

    @Test
    public void fiveStar() {
        solve("6..1...53..8...71..2..5....974..5......9.1......2..964....3..4..62...5..14...9..8",
                "697128453538496712421753896974365281286941375315287964859632147762814539143579628");
    }

    @Test
    public void tableTwo() {
        solve("..2.3...8.....8....31.2.....6..5.27..1.....5.2.4.6..31....8.6.5.......13..531.4..",
                "672435198549178362831629547368951274917243856254867931193784625486592713725316489");
    }

    @Test
    public void tableFive() {
        solve(".61..7..3.92..3..............853..........5.45....8....4......1...16.8..6........",
                "461987253792453168385216479128534796936721584574698312849375621253169847617842935");
    }

}
