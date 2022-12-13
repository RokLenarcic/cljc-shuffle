(ns cljc-shuffle.core
  (:require [cljc-shuffle.util :as util]))

(defn total-shuffles
  "Return the total count of different shuffles.

  Parameter is either a number of items or a coll of items."
  [items]
  (util/total-shuffles items))

(defn random-shuffle-idx
  "Returns a random shuffle index for items.

   items parameter is either a number of items or a coll of items

   the-random argument is an instance of java.util.Random you want to use/reuse,
   if not provided a new (SecureRandom/getInstanceStrong) will be used, which is
   rather slow. (8 us per call vs 0.25us per call if you reuse a secure random).

  Returned is the sequential number of shuffle. Use the number to shuffle the items."
  ([items] (util/random-shuffle-idx items nil))
  #?(:clj ([items the-random] (util/random-shuffle-idx items the-random))))

(defn apply-shuffle
  "Applies shuffle index to the coll.

  A shuffle index of 0 returns the coll unmodified.
  A shuffle index of total-shuffles - 1 returns the coll reversed."
  [idx coll]
  (let [radix (util/to-factoradic idx)]
    (cond
      ; takes care of empty coll edge case
      (= radix [0]) coll
      (< (count coll) (count radix)) (throw (ex-info
                                              "Shuffle idx higher than total number of shuffles"
                                              {:idx idx :coll coll}))
      :else (util/permute radix coll))))
