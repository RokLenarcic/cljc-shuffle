(ns cljc-shuffle.util
  #?(:clj (:import (clojure.lang BigInt)
                   (java.util ArrayList Random)
                   (java.security SecureRandom))))

#?(:clj (def default-random (SecureRandom/getInstanceStrong)))

(defn ->bigint
  "Convert given item to a bigint"
  [obj]
  #?(:clj (condp instance? obj
            BigInt obj
            BigInteger (BigInt/fromBigInteger obj)
            String (BigInt/fromBigInteger (BigInteger. ^String obj))
            Number (BigInt/fromLong (long obj)))
     :cljs (js/BigInt obj)))

(def fact (memoize #(apply * (map ->bigint (range 1 (inc %))))))
(defn total-shuffles
  "Return the total count of different shuffles.

   Parameter is either a number of items or a coll of items."
  [items]
  (fact (if (number? items) items (count items))))

(def bits-for-number
  (memoize (fn [n]
             (let [max-idx (- (->bigint n) (->bigint 1))]
               (if (< max-idx (->bigint 2))
                 1
                 #?(:clj (.bitLength ^BigInt max-idx)
                    :cljs (count (.toString max-idx 2))))))))

(defn to-factoradic
  "Convert a number to a vector that is the factoradic representation of that number,
  where each 'digit' is an int type."
  [n]
  (letfn [(reminder [n d]
            #?(:clj (long (mod n d))
               :cljs (js/Number.parseInt (.toString (mod n d)))))
          (quotient [n d]
            #?(:clj (quot n d)
               :cljs (/ n d)))
          (++ [i] #?(:clj (inc i)
                     :cljs (+ i (js/BigInt 1))))
          (step [n i]
            ; all numbers are bigint except for r
            (let [r (reminder n i)
                  q (quotient n i)]
              (if (= q (->bigint 0))
                [r] (conj (step q (++ i)) r))))]
    (step (->bigint n) (->bigint 1))))

(defn permute
  "Consume the index of permutation in factoradic vector representation and a list of items.
  Returns vector of permuted items."
  [factoradix-n items]
  (let [missing-zeroes (vec (repeat (- (count items) (count factoradix-n)) 0))
        permutes (into (vec missing-zeroes) factoradix-n)
        item-list #?(:clj (ArrayList.)
                     :cljs #js [])
        pop-idx (fn [l idx]
                     #?(:clj (.remove ^ArrayList l (int idx))
                        :cljs (let [x (aget l idx)
                                    _ (.splice l idx 1)]
                                x)))]
    (doseq [it items] (#?(:clj .add :cljs .push) item-list it))
    (reduce
      (fn [acc n] (conj acc (pop-idx item-list n))) [] permutes)))

#?(:cljs (defn binary [byt]
           (.substr (str "0000000" (.toString byt 2)) -8)))

#?(:cljs (defn random-big-int [bits]
           (let [arr (js/Uint8Array. (inc (int (/ bits 8))))
                 _ (js/window.crypto.getRandomValues arr)
                 binstring (.join (.call js/Array.prototype.map arr binary) "")]
             (js/BigInt (str "0b" (.substr binstring 0 bits))))))

(defn random-shuffle-idx [items the-random]
  (let [total (total-shuffles items)
        bits-required (bits-for-number total)
        g #?(:clj
             (let [^Random r (or the-random default-random)]
               (->bigint (BigInteger. (int bits-required) r)))
             :cljs (random-big-int bits-required))]
    (if (< g total) g (recur items the-random))))
