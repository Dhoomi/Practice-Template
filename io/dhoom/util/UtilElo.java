package io.dhoom.util;

public class UtilElo
{
    private static double[] getEstimations(final double rankingA, final double rankingB) {
        final double[] ret = new double[2];
        final double estA = 1.0 / (1.0 + Math.pow(10.0, (rankingB - rankingA) / 400.0));
        final double estB = 1.0 / (1.0 + Math.pow(10.0, (rankingA - rankingB) / 400.0));
        ret[0] = estA;
        ret[1] = estB;
        return ret;
    }
    
    private static int getConstant(final int ranking) {
        if (ranking < 1400) {
            return 32;
        }
        if (ranking >= 1400 && ranking < 1800) {
            return 24;
        }
        if (ranking >= 1800 && ranking < 2400) {
            return 16;
        }
        return 0;
    }
    
    public static int[] getNewRankings(final int rankingA, final int rankingB, final boolean victoryA) {
        final int[] elo = new int[2];
        final double[] estimates = getEstimations(rankingA, rankingB);
        final int newRankA = (int)(rankingA + getConstant(rankingA) * ((victoryA ? 1 : 0) - estimates[0]));
        elo[0] = Math.round(newRankA);
        elo[1] = Math.round(rankingB - (newRankA - rankingA));
        return elo;
    }
}
