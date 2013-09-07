/*
 *
 * Copyright å© 2010 Patrick McFarland
 *
 * This is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software. If not, see <http://www.gnu.org/licenses/>.
 */

package bloomjava.util.hash;

/**
 * MurmurHash3 implementation in Java, based on Austin Appleby's
 * <a href="https://code.google.com/p/smhasher/source/browse/trunk/MurmurHash3.cpp">original in C</a>
 *
 * Only implementing x64 version, because this should always be faster on 64 bit native processors,
 * even x64 being ran with a 32 bit OS.
 *
 * @author <a href="mailto:diablod3@gmail.com">Patrick McFarland</a>
 */
public class MurmurHash3 {
  static class State {
    long h1;
    long h2;

    long k1;
    long k2;

    long c1;
    long c2;
  }

  static long getblock(byte[] key, int i) {
    return
    (((long)(key[i+0] & 0xFF) << 56) |
     ((long)(key[i+1] & 0xFF) << 48) |
     ((long)(key[i+2] & 0xFF) << 40) |
     ((long)(key[i+3] & 0xFF) << 32) |
     ((long)(key[i+4] & 0xFF) << 24) |
     ((long)(key[i+5] & 0xFF) << 16) |
     ((long)(key[i+6] & 0xFF) <<  8) |
     ((long)(key[i+7] & 0xFF) <<  0)) & 0xFFFFFFFFL;
  }

  static void bmix64(State state) {
    state.k1 *= state.c1;
    state.k1  = (state.k1 << 23) | (state.k1 >>> 32 - 23);
    state.k1 *= state.c2;
    state.h1 ^= state.k1;
    state.h1 += state.h2;

    state.h2 = (state.h2 << 41) | (state.h2 >>> 32 - 41);

    state.k2 *= state.c2;
    state.k2  = (state.k2 << 23) | (state.k2 >>> 32 - 23);
    state.k2 *= state.c1;
    state.h2 ^= state.k2;
    state.h2 += state.h1;

    state.h1 = state.h1*3+0x52dce729;
    state.h2 = state.h2*3+0x38495ab5;

    state.c1 = state.c1*5+0x7b7d159c;
    state.c2 = state.c2*5+0x6bce6396;
  }

  static long fmix64(long k) {
    k ^= k >> 33;
    k *= 0xff51afd7ed558ccdL;
    k ^= k >> 33;
    k *= 0xc4ceb9fe1a85ec53L;
    k ^= k >> 33;

    return k;
  }

  /**
   * Hash a value using the x64 128 bit variant of MurmurHash3
   *
   * @param key value to hash
   * @param seed random value
   * @return 128 bit hashed key, in an array containing two longs
   */
  public static long[] MurmurHash3_x64_128(final byte[] key, final int seed) {
    State state = new State();

    state.h1 = 0x9368e53c2f6af274L ^ seed;
    state.h2 = 0x586dcd208f7cd3fdL ^ seed;

    state.c1 = 0x87c37b91114253d5L;
    state.c2 = 0x4cf5ad432745937fL;

    for(int i = 0; i < key.length && key.length >= 16; i += 16) {
      state.k1 = getblock(key, i);
      state.k2 = getblock(key, i + 8);

      bmix64(state);
    }

    state.k1 = 0;
    state.k2 = 0;

    int tail = key.length / 16;

    switch(key.length & 15) {
      case 15: state.k2 ^= (long)(key[tail + 14]) << 48;
      case 14: state.k2 ^= (long)(key[tail + 13]) << 40;
      case 13: state.k2 ^= (long)(key[tail + 12]) << 32;
      case 12: state.k2 ^= (long)(key[tail + 11]) << 24;
      case 11: state.k2 ^= (long)(key[tail + 10]) << 16;
      case 10: state.k2 ^= (long)(key[tail + 9]) << 8;
      case  9: state.k2 ^= (long)(key[tail + 8]) << 0;

      case  8: state.k1 ^= (long)(key[tail + 7]) << 56;
      case  7: state.k1 ^= (long)(key[tail + 6]) << 48;
      case  6: state.k1 ^= (long)(key[tail + 5]) << 40;
      case  5: state.k1 ^= (long)(key[tail + 4]) << 32;
      case  4: state.k1 ^= (long)(key[tail + 3]) << 24;
      case  3: state.k1 ^= (long)(key[tail + 2]) << 16;
      case  2: state.k1 ^= (long)(key[tail + 1]) << 8;
      case  1: state.k1 ^= (long)(key[tail + 0]) << 0;
        bmix64(state);
    }

    state.h2 ^= key.length;

    state.h1 += state.h2;
    state.h2 += state.h1;

    state.h1 = fmix64(state.h1);
    state.h2 = fmix64(state.h2);

    state.h1 += state.h2;
    state.h2 += state.h1;

    long[] out = { state.h1, state.h2 };
    return out;
  }

  /**
   * Hash a value using the x64 64 bit variant of MurmurHash3
   *
   * @param key value to hash
   * @param seed random value
   * @return 64 bit hashed key
   */
  public static long MurmurHash3_x64_64(final byte[] key, final int seed) {
    return MurmurHash3_x64_128(key, seed)[0];
  }

  /**
   * Hash a value using the x64 32 bit variant of MurmurHash3
   *
   * @param key value to hash
   * @param seed random value
   * @return 32 bit hashed key
   */
  public static int MurmurHash3_x64_32(final byte[] key, final int seed) {
    return (int) (MurmurHash3_x64_128(key, seed)[0] >>> 32);
  }

  /**
   * Hash a value using the x64 128 bit variant of MurmurHash3
   *
   * @param key value to hash
   * @param seed random value
   * @return 128 bit hashed key, in an array containing two longs
   */
  public static long[] MurmurHash3_x64_128(final long[] key, final int seed) {
    State state = new State();

    state.h1 = 0x9368e53c2f6af274L ^ seed;
    state.h2 = 0x586dcd208f7cd3fdL ^ seed;

    state.c1 = 0x87c37b91114253d5L;
    state.c2 = 0x4cf5ad432745937fL;

    for(int i = 0; i < key.length && key.length >= 2; i += 2) {
      state.k1 = key[i];
      state.k2 = key[i + 1];

      bmix64(state);
    }

    long tail = key[key.length - 1];

    if(key.length % 2 != 0) {
      state.k1 ^= tail;
      bmix64(state);
    }

    state.h2 ^= key.length * 8;

    state.h1 += state.h2;
    state.h2 += state.h1;

    state.h1 = fmix64(state.h1);
    state.h2 = fmix64(state.h2);

    state.h1 += state.h2;
    state.h2 += state.h1;

    long[] out = { state.h1, state.h2 };
    return out;
  }

  /**
   * Hash a value using the x64 64 bit variant of MurmurHash3
   *
   * @param key value to hash
   * @param seed random value
   * @return 64 bit hashed key
   */
  public static long MurmurHash3_x64_64(final long[] key, final int seed) {
    return MurmurHash3_x64_128(key, seed)[0];
  }

  /**
   * Hash a value using the x64 32 bit variant of MurmurHash3
   *
   * @param key value to hash
   * @param seed random value
   * @return 32 bit hashed key
   */
  public static int MurmurHash3_x64_32(final long[] key, final int seed) {
    return (int) (MurmurHash3_x64_128(key, seed)[0] >>> 32);
  }
}
