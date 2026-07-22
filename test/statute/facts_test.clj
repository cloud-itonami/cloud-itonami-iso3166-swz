(ns statute.facts-test
  (:require [clojure.string :as str]
            [clojure.test :refer [deftest is]]
            [statute.facts :as facts]))

(deftest swz-has-spec-basis
  (let [sb (facts/spec-basis "SWZ")]
    (is (= 3 (count sb)))
    (is (every? #(str/starts-with? (:statute/url %) "https://") sb))
    (is (every? :statute/law-number sb))))

(deftest unknown-jurisdiction-has-no-spec-basis
  (is (nil? (facts/spec-basis "ATL")))
  (is (nil? (facts/spec-basis "ZZZ"))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["SWZ" "JPN" "ATL"])]
    (is (= 3 (:requested c)))
    (is (= 1 (:covered c)))
    (is (= ["ATL" "JPN"] (:missing-jurisdictions c)))))

(deftest by-topic-filters
  (is (= ["swz.employment-act-1980"]
         (mapv :statute/id (facts/by-topic "SWZ" :labor))))
  (is (= ["swz.value-added-tax-act-2011"]
         (mapv :statute/id (facts/by-topic "SWZ" :tax))))
  (is (= ["swz.companies-act-2009"]
         (mapv :statute/id (facts/by-topic "SWZ" :corporate-governance))))
  (is (empty? (facts/by-topic "ATL" :labor))))
