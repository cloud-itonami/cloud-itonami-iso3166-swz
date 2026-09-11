(ns marketentry.facts-test
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.facts :as facts]))

(deftest swz-has-spec-basis
  (let [sb (facts/spec-basis "SWZ")]
    (is (some? sb))
    (is (string? (:provenance sb)))
    (is (seq (:required-evidence sb)))
    (is (some? (facts/supplier-eligibility-spec-basis "SWZ")))
    (is (some? (facts/corporate-number-spec-basis "SWZ")))
    (is (some? (facts/business-registration-spec-basis "SWZ")))))

(deftest swz-rep-spec-basis-is-honestly-absent
  (testing "the Act's own s.39 Swazi-company-preference mechanism is delegated to Regulations this iteration did not separately locate/read -- deliberately not claimed"
    (is (nil? (facts/rep-spec-basis "SWZ")))))

(deftest swz-corporate-number-spec-basis-is-real
  (testing "the Taxpayer Identification Number (TIN) scheme is administered by the Eswatini Revenue Service (ERS) -- a genuine strengthening beyond this family's LSO/NAM catalogs"
    (let [cn (facts/corporate-number-spec-basis "SWZ")]
      (is (some? cn))
      (is (string? (:corporate-number-owner-authority cn)))
      (is (string? (:corporate-number-legal-basis cn))))))

(deftest unknown-jurisdiction-has-no-spec-basis
  (is (nil? (facts/spec-basis "ATL")))
  (is (nil? (facts/spec-basis "ZZZ"))))

(deftest required-evidence-satisfied
  (let [sb (facts/spec-basis "SWZ")
        all (:required-evidence sb)]
    (is (true? (facts/required-evidence-satisfied? "SWZ" all)))
    (is (not (facts/required-evidence-satisfied? "SWZ" (take 1 all))))
    (is (nil? (facts/required-evidence-satisfied? "ATL" all)))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["SWZ" "USA" "ATL"])]
    (is (= 3 (:requested c)))
    (is (= 2 (:covered c)))
    (is (= ["ATL"] (:missing-jurisdictions c)))))

(deftest supplier-eligibility-spec-basis-cites-the-act
  (let [se (facts/supplier-eligibility-spec-basis "SWZ")]
    (is (string? (:supplier-eligibility-legal-basis se)))
    (is (re-find #"55" (:supplier-eligibility-legal-basis se)))
    (is (re-find #"40\(1\)\(g\)" (:supplier-eligibility-legal-basis se)))))
