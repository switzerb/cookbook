package com.brennaswitzer.cookbook.sudoku;

import java.util.function.BooleanSupplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class SudokuTest {

    public static final String ONE_STAR = ".549..67.8..5...4...68243...4...5..2.32...19.7..3...8...54139...2...9..3.19..786.";
    public static final String FIVE_STAR = "6..1...53..8...71..2..5....974..5......9.1......2..964....3..4..62...5..14...9..8";
    public static final String TABLE_FIVE = ".61..7..3.92..3..............853..........5.45....8....4......1...16.8..6........";
    public static final String TWO_STAR = ".3.9.8...52..43....8....9.3.16.9....3.......1....3.62.7.2....9....87..42...2.5.6.";

    protected abstract Solver getSolver(String puzzle);

    protected void check(String puzzle, String solution) {
        int dim = (int) Math.sqrt(puzzle.length());
        assertEquals(dim * dim, puzzle.length());
        assertEquals(puzzle.length(), solution.length());
        for (int i = 0, l = puzzle.length(); i < l; i++) {
            char c = puzzle.charAt(i);
            if (Sudoku.isEmptyIndicator(c)) continue;
            assertEquals(c + " in position " + i + " is inconsistent", "" + solution.charAt(i), "" + c);
        }
    }

    protected void solve(String puzzle, String solution) {
        System.out.printf("puzzle  : %s%n", Sudoku.boardWithCount(puzzle));
        if (solution != null) check(puzzle, solution);
        Solver solver = getSolver(puzzle);
        String solved = solver.getBoard();
        System.out.printf("solved  : %s%n", solver);
        if (solution != null) {
            System.out.printf("solution: %s%n", solution);
            StringBuilder diff = new StringBuilder();
            int count = 0;
            for (int i = 0; i < solution.length(); i++) {
                if (solution.charAt(i) == solved.charAt(i)) {
                    diff.append(' ');
                } else {
                    diff.append('^');
                    count += 1;
                }
            }
            if (count > 0) {
                diff.append(" (").append(count).append(')');
                System.out.printf("          %s%n", diff);
            }
        }
        if (solver.getFrameCount() > 1) {
            System.out.printf("frames  : %,d%n", solver.getFrameCount());
        }
        System.out.printf("elapsed : %,d μs%n", solver.getElapsed() / 1000);
        if (solution != null) {
            check(solved, solution);
            assertEquals(solution, solved);
        } else {
            assertTrue(solved, solver.isSolved());
        }
    }

    protected void benchmark(String label, int iterations, BooleanSupplier work) {
        for (int i = 0, l = iterations / 25 + 1; i < l; i++)
            assertTrue("Failed on warmup " + i, work.getAsBoolean());
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++)
            assertTrue("Failed on iteration " + i, work.getAsBoolean());
        long elapsed = System.nanoTime() - start;
        System.out.printf("BENCHMARK[%s]: %,d μs, over %d iterations%n", label, elapsed / 1000 / iterations, iterations);
    }

    public void _4x4() {
        solve("4213132.24313142",
                "4213132424313142");
    }

    public void blank() {
        // linear: 123456789456789123789123456214365897365897214897214365531642978642978531978531642
        solve(".................................................................................",
                null);
    }

    public void oneStar() {
        solve(ONE_STAR,
                "254931678893576241176824359948165732532748196761392485685413927427689513319257864");
    }

    public void twoStar() {
        solve(TWO_STAR,
                "637958214529143786481726953216497835395682471874531629752364198163879542948215367");
    }

    public void threeStar() {
        solve(".263..7.....1....941.7.......78......82...15......48.......5.171....6.....9..368.",
                "926358741873142569415769238547831926382697154691524873264985317138276495759413682");
    }

    public void fourStar() {
        solve(".4......8.9...451...2...6..37...2..1...3.7...2..9...53..9...8...836...4.5......2.",
                "145276938697834512832159674378562491951347286264981753429713865783625149516498327");
    }

    public void fiveStar() {
        solve(FIVE_STAR,
                "697128453538496712421753896974365281286941375315287964859632147762814539143579628");
    }

    public void tableTwo() {
        solve("..2.3...8.....8....31.2.....6..5.27..1.....5.2.4.6..31....8.6.5.......13..531.4..",
                "672435198549178362831629547368951274917243856254867931193784625486592713725316489");
    }

    public void tableFive() {
        solve(TABLE_FIVE,
                "461987253792453168385216479128534796936721584574698312849375621253169847617842935");
    }

    public void beginnerMonster() {
        solve("B...A.96.1....5." +
                        "....7.C...2D1B.0" +
                        "6....5.8..BAD.EF" +
                        "91.DB....F.5...." +
                        "" +
                        ".4......E3....6." +
                        "....94....AC.3.." +
                        ".8D1.3B..7.F9..4" +
                        "3E.5.C..6....8F1" +
                        "" +
                        "...8D..4....3.C9" +
                        ".....A.5.DC..E7." +
                        "..........6..F.." +
                        "...F.B.9..78A..5" +
                        "" +
                        "49....F.AE.B81D2" +
                        "D713.....9...4.C" +
                        ".....EDC85.17..." +
                        "...E....3.....A.",
                "BF04AD96C18E2753" +
                        "85EA7FC3462D1B90" +
                        "6237154890BADCEF" +
                        "91CDB2E07F35468A" +
                        "" +
                        "F4B9805AE312CD67" +
                        "7026941FD8AC53BE" +
                        "C8D163BE075F9A24" +
                        "3EA52C7D6B9408F1" +
                        "" +
                        "5B78D164FAE032C9" +
                        "1342FA05BDC96E78" +
                        "AC90E7825463BF1D" +
                        "ED6FCB391278A045" +
                        "" +
                        "495C36F7AE0B81D2" +
                        "D71358AB29F6E40C" +
                        "2AFB0EDC85417936" +
                        "068E49213CD7F5AB");
    }

    public void expertMonster() {
        solve("8...D..0A..C...." +
                        "....AF.857B....." +
                        "..AB..3..2..8.F." +
                        ".C......0.D...9E" +
                        "" +
                        "0...3.A1.....B8." +
                        "...3.....A.D..C." +
                        "2....B....4.7..." +
                        ".9..064..FCE1..." +
                        "" +
                        "...8...C3..1E..." +
                        "..E...FD.8.B...." +
                        ".7..B3.264.AD0.." +
                        ".6095.......2..1" +
                        "" +
                        "CA..6.1..9...D.." +
                        "9E.....A..5FC6.." +
                        "6.....C...1....4" +
                        "...4E.73.....52F",
                "8276DE90A1FC543B" +
                        "E39DAF2857B4610C" +
                        "40ABC135E26987FD" +
                        "5CF174B603D8A29E" +
                        "" +
                        "04DE3CA19675FB82" +
                        "7163F259BA8D4EC0" +
                        "2FC58BDE10437A69" +
                        "B98A06472FCE13D5" +
                        "" +
                        "DB48906C3521EF7A" +
                        "A5E217FDC80B3946" +
                        "F71CB3E2649AD058" +
                        "36095A84FDE72CB1" +
                        "" +
                        "CA50681F4932BDE7" +
                        "9E274D0A8B5FC613" +
                        "6D3F25CB7E1098A4" +
                        "18B4E973DCA6052F");
    }

}
