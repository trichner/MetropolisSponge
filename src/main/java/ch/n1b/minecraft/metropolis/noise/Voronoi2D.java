/*
 * Copyright (C) 2003, 2004 Jason Bevins (original libnoise code)
 * Copyright © 2010 Thomas J. Hodge (java port of libnoise)
 * Copyright © 2016 Thomas Richner
 *
 * This file was part of libnoiseforjava.
 *
 * libnoiseforjava is a Java port of the C++ library libnoise, which may be found at
 * http://libnoise.sourceforge.net/.  libnoise was developed by Jason Bevins, who may be
 * contacted at jlbezigvins@gmzigail.com (for great email, take off every 'zig').
 * Porting to Java was done by Thomas Hodge, who may be contacted at
 * libnoisezagforjava@gzagmail.com (remove every 'zag').
 *
 * libnoiseforjava is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * libnoiseforjava is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * libnoiseforjava.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package ch.n1b.minecraft.metropolis.noise;

import java.util.Random;
import java.util.function.BiFunction;

/**
 * This is a Voronoi noise generator, originally from https://github.com/TJHJava/libnoiseforjava
 * It was modified to work in a similar way to the bukkit noise generators, and to support
 * octaves and 2d noise, by mncat77 and jtjj222.
 * <p>
 * To use octaves, use the VoronoiOctaveGenerator class.
 */
public class Voronoi2D {

    /// Noise module that outputs Voronoi cells.
    ///
    /// In mathematics, a <i>Voronoi cell</i> is a region containing all the
    /// points that are closer to a specific <i>seed point</i> than to any
    /// other seed point.  These cells mesh with one another, producing
    /// polygon-like formations.
    ///
    /// By default, this noise module randomly places a seed point within
    /// each unit cube.  By modifying the <i>frequency</i> of the seed points,
    /// an application can change the distance between seed points.  The
    /// higher the frequency, the closer together this noise module places
    /// the seed points, which reduces the size of the cells.  To specify the
    /// frequency of the cells, call the setFrequency() method.
    ///
    /// This noise module assigns each Voronoi cell with a random constant
    /// value from a coherent-noise function.  The <i>displacement value</i>
    /// controls the range of random values to assign to each cell.  The
    /// range of random values is +/- the displacement value.  Call the
    /// setDisplacement() method to specify the displacement value.
    ///
    /// To modify the random positions of the seed points, call the SetSeed()
    /// method.
    ///
    /// This noise module can optionally add the distance from the nearest
    /// seed to the output value.  To enable this feature, call the
    /// enableDistance() method.  This causes the points in the Voronoi cells
    /// to increase in value the further away that point is from the nearest
    /// seed point.

    //for speed, we can approximate the sqrt term in the distance funtions
    private static final double SQRT_2 = 1.4142135623730950488;
    private static final double SQRT_3 = 1.7320508075688772935;
    private final double frequency;

    //You can either use the feature point height (for biomes or solid pillars), or the distance to the feature point
    private boolean useDistance = false;

    private long seed;

    private DistanceNorm distanceNorm;


    public Voronoi2D(long seed, double frequency, DistanceNorm distanceNorm) {
        this.seed = seed;
        this.distanceNorm = distanceNorm;
        this.frequency = frequency;
    }

    private double getDistance(double xDist, double zDist) {
        return distanceNorm.apply(xDist, zDist);
    }

    public Point noise(double x, double z) {
        x *= frequency;
        z *= frequency;

        int xInt = (x > .0 ? (int) x : (int) x - 1);
        int zInt = (z > .0 ? (int) z : (int) z - 1);

        double minDist = 32000000.0;

        double xCandidate = 0;
        double zCandidate = 0;

        for (int zCur = zInt - 2; zCur <= zInt + 2; zCur++) {
            for (int xCur = xInt - 2; xCur <= xInt + 2; xCur++) {

                double xPos = xCur + valueNoise2D(xCur, zCur, seed);
                double zPos = zCur + valueNoise2D(xCur, zCur, new Random(seed).nextLong());
                double xDist = xPos - x;
                double zDist = zPos - z;
                double dist = distanceNorm.apply(xDist, zDist); //xDist * xDist + zDist * zDist;

                if (dist < minDist) {
                    minDist = dist;
                    xCandidate = xPos;
                    zCandidate = zPos;
                }
            }
        }

        double noise = (Voronoi2D.valueNoise2D(
                (int) (Math.floor(xCandidate)),
                (int) (Math.floor(zCandidate)), seed));
        return new Point(noise, minDist);
    }

    /**
     * To avoid having to store the feature points, we use a hash function
     * of the coordinates and the seed instead. Those big scary numbers are
     * arbitrary primes.
     */
    private static double valueNoise2D(int x, int z, long seed) {
        long n = (1619 * x + 6971 * z + 1013 * seed) & 0x7fffffff;
        n = (n >> 13) ^ n;
        return 1.0 - ((double) ((n * (n * n * 60493 + 19990303) + 1376312589) & 0x7fffffff) / 1073741824.0);
    }

    public interface DistanceNorm extends BiFunction<Double, Double, Double> {
    }

    public static class Point {
        public final double field;
        public final double distance;

        public Point(double field, double distance) {
            this.field = field;
            this.distance = distance;
        }
    }
}