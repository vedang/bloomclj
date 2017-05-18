# bloomclj

A Bloom Filter implementation in Clojure.

## Usage

```clj
[me.vedang/bloomclj "0.0.2"]
```

You can find the annotated docs for Bloomclj [here](http://vedang.me/bloomclj/)

### Using a simple Bloom Filter
```clj
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
     "OK"
     user> (add tbf "world")
     "OK"
     user> (maybe-contains? tbf "hello")
     true
     user> (maybe-contains? tbf "goodbye")
     false
     user> (clear tbf)
     "OK"
     user> (maybe-contains? tbf "hello")
     false
     user>
```

### Using other backing stores

An example implementation of a Redis Backed Bloom Filter is provided [here](http://vedang.me/bloomclj/#bloomclj.redis-backed).

## Contributors

- Vedang Manerikar ([@vedang](https://github.com/vedang))
- Kiran Kulkarni ([@kirankulkarni](https://github.com/kirankulkarni))
- Chris Truter ([@crisptrutski](https://github.com/crisptrutski))

## License

Copyright © 2013 Vedang Manerikar

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
