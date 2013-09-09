# bloomclj

A Bloom Filter implementation in Clojure.

## Usage

You can find the annotated docs for Bloomclj [here](http://vedang.github.io/bloomclj/)

### Using a simple Bloom Filter
     user> (require '[bloomclj.core
                      :refer [add maybe-contains? clear]])
     nil
     user> (require '[bloomclj.transient
                      :refer [transient-bloom-filter]])
     nil
     user> (transient-bloom-filter 100000 0.01)
     #<TransientBloomFilter bloomclj.transient.TransientBloomFilter@7130779c>
     user> (def tbf *1)
     #'user/tbf
     user> (add tbf "hello")
     nil
     user> (add tbf "world")
     nil
     user> (maybe-contains? tbf "hello")
     true
     user> (maybe-contains? tbf "goodbye")
     false
     user> (clear tbf)
     nil
     user> (maybe-contains? tbf "hello")
     false
     user>

### Using other backing stores

An example implementation of a Redis Backed Bloom Filter is provided [here](http://vedang.github.io/bloomclj/).

## Contributors

Vedang Manerikar (@vedang)
Kiran Kulkarni (@kirankulkarni)

## License

Copyright © 2013 Vedang Manerikar

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
