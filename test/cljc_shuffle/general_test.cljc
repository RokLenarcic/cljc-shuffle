(ns cljc-shuffle.general-test
  (:require [cljc-shuffle.core :as core]
            [cljc-shuffle.util :as util]
            [clojure.test :as t])
  #?(:clj (:import (java.util Random))))

#?(:clj
   (t/deftest bigint-constructor
     (t/is (= 100000000000000N (util/->bigint "100000000000000")))
     (t/is (= 10N (util/->bigint 10)))
     (t/is (= 100000000000000N (util/->bigint 100000000000000N))))
   :cljs
   (t/deftest bigint-constructor
     (t/is (= (js/BigInt "100000000000000") (util/->bigint "100000000000000")))
     (t/is (= (js/BigInt "10") (util/->bigint 10)))
     (t/is (= (js/BigInt "100000000000000") (util/->bigint (js/BigInt "100000000000000"))))))

(t/deftest fact-test
  (t/is (= (util/->bigint 1) (util/fact 1)))
  (t/is (= (util/->bigint 2) (util/fact 2)))
  (t/is (= (util/->bigint "80658175170943878571660636856403766975289505440883277824000000000000")
           (util/fact 52))))

(t/deftest total-shuffles-test
  (t/is (= (core/total-shuffles 7) (util/fact 7)))
  (t/is (= (core/total-shuffles [1 2 3 4 5 6]) (util/fact 6))))

(t/deftest bits-for-number-test
  (t/is (= (util/bits-for-number 7) 3))
  (t/is (= (util/bits-for-number 8) 3))
  (t/is (= (util/bits-for-number 9) 4))
  (t/is (= (util/bits-for-number 0) 1))
  (t/is (= (util/bits-for-number 1) 1))
  (t/is (= (util/bits-for-number 2) 1))
  (t/is (= (util/bits-for-number (util/fact 52)) 226)))

(t/deftest factoradix-test
  (t/is (= (util/to-factoradic 7) [1 0 1 0]))
  (t/is (= (util/to-factoradic 8) [1 1 0 0]))
  (t/is (= (util/to-factoradic 23) [3 2 1 0]))
  (t/is (= (util/to-factoradic 24) [1 0 0 0 0])))

(t/deftest permute-test
  (t/is (= (util/permute [3 2 1 0] [1 2 3 4]) [4 3 2 1]))
  (t/is (= (util/permute [0 0 0 0] [1 2 3 4]) [1 2 3 4]))
  (t/is (= (util/permute [0] [1 2 3 4]) [1 2 3 4]))
  (t/is (= (util/permute [1 0] [1 2 3 4]) [1 2 4 3])))

(t/deftest random-shuffle-idx-test
  (t/is (< (core/random-shuffle-idx [1 2 3 4]) 24))
  (t/is (< (core/random-shuffle-idx 4) 24))
  (t/is (= (core/random-shuffle-idx 1) (util/->bigint 0)))
  (t/is (= (core/random-shuffle-idx 0) (util/->bigint 0)))
  #?(:clj (let [r (Random. 44)]
            (t/is (= (core/random-shuffle-idx 4 r) 8N))
            (t/is (= (core/random-shuffle-idx 4 r) 22N))
            (t/is (= (core/random-shuffle-idx 52 r) 8143478514292146865746785770413710842978842226310789872439825720185N)))))

(t/deftest apply-shuffle-test
  (t/is (= (core/apply-shuffle 0 [1 2 3 4]) [1 2 3 4]))
  (t/is (= (core/apply-shuffle 0 [1]) [1]))
  (t/is (= (core/apply-shuffle 0 []) []))
  (t/is (= (core/apply-shuffle (util/->bigint 0) [1 2 3 4]) [1 2 3 4]))
  (t/is (= (core/apply-shuffle "0" [1 2 3 4]) [1 2 3 4]))
  (t/is (= (core/apply-shuffle (- (core/total-shuffles [1 2 3 4])
                                  (util/->bigint 1))
                               [1 2 3 4])
           [4 3 2 1])))
