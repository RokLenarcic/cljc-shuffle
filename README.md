# CLJC Shuffle

[![Clojars Project](https://img.shields.io/clojars/v/org.clojars.roklenarcic/cljc-shuffle.svg)](https://clojars.org/org.clojars.roklenarcic/cljc-shuffle)

**Shuffle items with the following features:**
- high strength random (cryptographic quality), should be able to produce all 52! shuffles of a standard card deck
- serializable, repeatable shuffles (you can apply same shuffle multiple times)
- works in Clojure and ClojureScript

## Why not `clojure.core/shuffle`

- It doesn't say which shuffle was generated
- Shuffling 52 cards takes about 226 bits of entropy, neither Clojure nor ClojureScript `shuffle` use that much entropy hence they will not generate all possible shuffles.
- ClojureScript version doesn't correct the bias hence the shuffle is not uniform

## Usage

```(require '[cljc-shuffle.core :as csc])```

**Generate random shuffle index**

```clojure
;; get random shuffle of 52 elements
(csc/random-shuffle-idx 52)
;; a bigint that describes the shuffle, which you can store or persist
=> 31954637225415380215497917762362973996613231094102326227370763261199N

;; works in CLJS, uses new(ish) JS built-in BigInt
(csc/random-shuffle-idx 52)
=> #object[BigInt 24443099249246604656147775690120451179003734729161700176368444126819]
(str (csc/random-shuffle-idx 52))
=> "26069843544380518745708628811963881129170908585662775621115121751909"

;; instead of item count you can specify an existing collection
(csc/random-shuffle-idx [1 2 3])
=> 5N

;; CLJ version has arity where you can supply `java.util.Random` object,
;; to use instead of the default (SecureRandom/getInstanceStrong)
(csc/random-shuffle-idx [1 2 3] (Random. 0))
=> 0N
(csc/random-shuffle-idx [1 2 3] (Random. 0))
=> 0N
;; weak random with specific seed is useful for testing
```

**Apply the shuffle index to collection:**

```clojure
(let [items [1 2 3 4 5 6 7]]
  (csc/apply-shuffle
    (csc/random-shuffle-idx items)
    items))
=> [7 5 3 4 2 1 6]

;; the shuffle index source can be anything, not just random
;; you can specify is as:

;; regular number
(csc/apply-shuffle 55 [1 2 3 4 5 6 7])
=> [1 2 5 4 3 7 6]

;; clojure bitint or JS BigInt
(csc/apply-shuffle (js/BigInt 55) [1 2 3 4 5 6 7])
(csc/apply-shuffle 55N [1 2 3 4 5 6 7])

;; or as string on both platforms
(csc/apply-shuffle "55" [1 2 3 4 5 6 7])
```

The shuffles are ordered:
```clojure
;; shuffle 0 doesn't change anything
(csc/apply-shuffle 0 [1 2 3 4 5 6 7])
=> [1 2 3 4 5 6 7]

;; shuffle '(num shuffles - 1)' reverses the coll
(csc/apply-shuffle (dec (csc/total-shuffles 7)) [1 2 3 4 5 6 7])
=> [7 6 5 4 3 2 1]

;; you can shuffle n last items only:
(let [items [1 2 3 4 5 6 7]]
  (csc/apply-shuffle
    ;; create an idx for shuffle of 3 elements, but apply to all 7
    (csc/random-shuffle-idx 3)
    items))
=> [1 2 3 4 7 6 5]
```

## License

Copyright © 2021 Rok Lenarčič

Licensed under the term of the Eclipse Public License - v 2.0, see LICENSE.
