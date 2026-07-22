(ns marketentry.registry-test
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.registry :as registry]))

(deftest engagement-fee-recompute
  (let [e {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12 :claimed-fee 860000.0}]
    (is (== 860000.0 (registry/compute-engagement-fee e)))
    (is (true? (registry/engagement-fee-matches-claim? e))))
  (let [bad {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12 :claimed-fee 999000.0}]
    (is (false? (registry/engagement-fee-matches-claim? bad)))))

(deftest register-draft-and-submit
  (let [d (registry/register-draft "eng-1" "SWZ" 0)
        s (registry/register-submit "eng-1" "SWZ" 0)]
    (is (= "SWZ-DFT-000000" (get d "draft_number")))
    (is (= "SWZ-SUB-000000" (get s "submit_number")))
    (is (nil? (get-in d ["certificate" "proof"])))
    (is (= "draft-unsigned" (get-in s ["certificate" "status"])))))

(deftest register-requires-ids
  (is (thrown? Exception (registry/register-draft "" "SWZ" 0)))
  (is (thrown? Exception (registry/register-submit "eng-1" "" 0))))

(deftest supplier-ineligible-under-suspension-grounds
  (testing "a discretionary ESPPRA suspension (s.55(1)-(3)) alone is disqualifying"
    (is (true? (registry/supplier-ineligible-under-suspension?
                {:esppra-active-suspension? true :tender-defaulter-register? false}))))
  (testing "an automatic Register for Tender Defaulters endorsement (s.55(6)-(7)) alone is disqualifying"
    (is (true? (registry/supplier-ineligible-under-suspension?
                {:esppra-active-suspension? false :tender-defaulter-register? true}))))
  (testing "neither ground present -> eligible"
    (is (false? (registry/supplier-ineligible-under-suspension?
                 {:esppra-active-suspension? false :tender-defaulter-register? false}))))
  (testing "missing ground truth fails closed (not ineligible by default, not thrown)"
    (is (false? (registry/supplier-ineligible-under-suspension? {})))))

(deftest supplier-eligibility-mismatch-claim
  (testing "claiming eligible while under an active ESPPRA suspension -> mismatch"
    (is (true? (registry/supplier-eligibility-mismatch-claim?
                {:claimed-eligible? true :esppra-active-suspension? true :tender-defaulter-register? false}))))
  (testing "claiming eligible while endorsed on the Register for Tender Defaulters -> mismatch"
    (is (true? (registry/supplier-eligibility-mismatch-claim?
                {:claimed-eligible? true :esppra-active-suspension? false :tender-defaulter-register? true}))))
  (testing "claiming eligible with neither disqualifying ground -> not flagged"
    (is (false? (registry/supplier-eligibility-mismatch-claim?
                 {:claimed-eligible? true :esppra-active-suspension? false :tender-defaulter-register? false}))))
  (testing "honestly declaring ineligible is never flagged, even with a real disqualifying ground"
    (is (false? (registry/supplier-eligibility-mismatch-claim?
                 {:claimed-eligible? false :esppra-active-suspension? true :tender-defaulter-register? false})))))
