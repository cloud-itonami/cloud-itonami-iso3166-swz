(ns marketentry.registry
  "Pure-function market-entry filing-draft + filing-submit record
  construction -- an append-only market-entry book-of-record draft.

  Like every sibling actor's registry, there is no single international
  reference-number standard for a public-procurement market-entry
  filing -- every jurisdiction assigns its own format. This namespace
  does NOT invent one; it builds a jurisdiction-scoped sequence number
  and validates the record's required fields, the same honest,
  non-fabricating discipline `marketentry.facts` uses.

  `engagement-fee-matches-claim?` is an HONEST reapplication of the
  SAME ground-truth-recompute DISCIPLINE sibling actors use (verify a
  claimed monetary total against the entity's own recorded quantity x
  unit fields), reapplied to a market-entry engagement fee line.

  `supplier-ineligible-under-suspension?` /
  `supplier-eligibility-mismatch-claim?` are THIS vertical's own new
  ground-truth check, grounding SWZ's flagship governor check
  (`marketentry.governor/supplier-eligibility-mismatch-violations`):
  the Public Procurement Act No. 7 of 2011 (own primary text, fetched
  directly and read in full, see `marketentry.facts`) s.40(1)(g)
  requires a tenderer to demonstrate 'it is not subject to suspension
  in accordance with section 55, and none of its directors or officers
  have been involved in a tenderer or supplier currently subject to
  suspension' before it is eligible to participate in public
  procurement at all. s.55 lays out TWO independently-grounded
  disqualifying conditions:

    1. a DISCRETIONARY Agency suspension under s.55(1)-(3) -- requires
       prior notice and an opportunity to respond (s.55(2)), and is
       bounded to a minimum of one year and a maximum of five years
       (s.55(3));
    2. an AUTOMATIC suspension under s.55(6)-(7) -- triggered purely by
       the tenderer/supplier/person being endorsed on the Register for
       Tender Defaulters under a DIFFERENT Act (the Prevention of
       Corruption Act, 2006), EXEMPT from both the notice-and-response
       procedure ('Notwithstanding the provisions of subsection (2)')
       and the 1-5 year bound ('Notwithstanding the provisions of
       subsection (3), the suspension shall be for the full duration of
       the time the tenderer, supplier or person is endorsed on the
       Register').

  This is a GENUINELY DIFFERENT check SHAPE than every prior iso3166
  sibling this repo mirrors that this iteration itself has read:
  Lesotho's Contractors Registration Certificate mechanism is a
  DISCRETE-CATEGORY -> FIXED-CONSTANT LOOKUP-TABLE EQUALITY test;
  Namibia's Board-routing mechanism is a TWO-DIMENSIONAL (category x
  contract-type) THRESHOLD LOOKUP; Botswana's citizen-reservation
  mechanism is an ORDERED FOUR-TIER classification recompute. Eswatini's
  own mechanism is none of these: it is a DISQUALIFICATION-GROUNDS gate
  -- an OR over two independently-sourced boolean grounds, one
  discretionary-and-procedurally-bounded, one automatic-and-cross-
  Act-referenced -- tested against the engagement's own claimed
  eligibility self-certification (s.40(2): 'All invitation documents
  shall state the eligibility requirements'), not a lookup, not a
  threshold, not a tier ranking.

  This namespace is pure data + pure functions -- no I/O, no network
  call to any real ESPPRA portal. It builds the RECORD an operator
  would keep, not the act of submitting a portal registration itself
  (that is `marketentry.operation`'s `:filing/submit`, always
  human-gated -- see README Actuation)."
  (:require [clojure.string :as str]))

(defn- unsigned-certificate
  "Every certificate this actor produces is UNSIGNED -- signature is
  the market-entry operator's act, not this actor's."
  [kind subject record-id]
  {"@context" ["https://www.w3.org/ns/credentials/v2"]
   "type" ["VerifiableCredential" kind]
   "credentialSubject" {"id" subject "record" record-id}
   "proof" nil
   "issued_by_registry" false
   "status" "draft-unsigned"})

(defn- zero-pad [n w]
  (let [s (str n)]
    (str (apply str (repeat (max 0 (- w (count s))) "0")) s)))

(defn compute-engagement-fee
  "The ground-truth engagement fee for `engagement`'s own `:base-fee`
  and `:monitoring-months` x `:monthly-rate` -- a single flat
  base + months x rate calculation, not a full pricing engine."
  [{:keys [base-fee monthly-rate monitoring-months]}]
  (+ (double base-fee)
     (* (double monthly-rate) (double monitoring-months))))

(defn engagement-fee-matches-claim?
  "Does `engagement`'s own `:claimed-fee` equal the independently
  recomputed `compute-engagement-fee`?"
  [{:keys [claimed-fee] :as engagement}]
  (== (double claimed-fee) (compute-engagement-fee engagement)))

(defn supplier-ineligible-under-suspension?
  "INDEPENDENTLY recompute whether `engagement`'s own declared
  suspension ground truth makes it ineligible under the Public
  Procurement Act No. 7 of 2011 s.40(1)(g) -- an OR over the Act's own
  two independently-grounded disqualifying conditions:

    - `:esppra-active-suspension?` -- a discretionary ESPPRA suspension
      under s.55(1)-(3), currently in force;
    - `:tender-defaulter-register?` -- an AUTOMATIC suspension under
      s.55(6)-(7), triggered by endorsement on the Register for Tender
      Defaulters (Prevention of Corruption Act, 2006), independent of
      any formal ESPPRA suspension action.

  Either ground alone is sufficient; this mirrors the Act's own
  disjunctive language ('it is not subject to suspension ... and none
  of its directors or officers have been involved in a tenderer or
  supplier currently subject to suspension')."
  [{:keys [esppra-active-suspension? tender-defaulter-register?]}]
  (boolean (or esppra-active-suspension? tender-defaulter-register?)))

(defn supplier-eligibility-mismatch-claim?
  "Does `engagement` claim `:claimed-eligible? true` (a tenderer's own
  s.40(2) self-certification) while the INDEPENDENTLY recomputed
  `supplier-ineligible-under-suspension?` is true? A tenderer that
  honestly declares itself ineligible (or has no suspension ground
  truth at all) is never flagged by this check -- only a false
  eligibility claim against a real, ground-truth disqualifying
  condition is a mismatch."
  [{:keys [claimed-eligible?] :as engagement}]
  (boolean (and claimed-eligible? (supplier-ineligible-under-suspension? engagement))))

(defn register-draft
  "Validate + construct the FILING-DRAFT registration DRAFT -- the
  market-entry operator's own act of preparing an ESPPRA tender
  response package. Pure function -- does not touch any real ESPPRA
  system."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "draft: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "draft: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "draft: sequence must be >= 0" {})))
  (let [draft-number (str (str/upper-case jurisdiction) "-DFT-" (zero-pad sequence 6))
        record {"record_id" draft-number
                "kind" "filing-draft"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "draft_number" draft-number
     "certificate" (unsigned-certificate "FilingDraft" draft-number draft-number)}))

(defn register-submit
  "Validate + construct the FILING-SUBMIT registration DRAFT -- the
  market-entry operator's own act of actually submitting an ESPPRA
  tender response (always human-gated upstream)."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "submit: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "submit: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "submit: sequence must be >= 0" {})))
  (let [submit-number (str (str/upper-case jurisdiction) "-SUB-" (zero-pad sequence 6))
        record {"record_id" submit-number
                "kind" "filing-submit"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "submit_number" submit-number
     "certificate" (unsigned-certificate "FilingSubmit" submit-number submit-number)}))

(defn append [history result]
  (conj (vec history) (get result "record")))
