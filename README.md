# cloud-itonami-iso3166-swz

**SWZ**: Eswatini.

- Public Procurement Act No. 7 of 2011 (Eswatini Public Procurement
  Regulatory Agency (ESPPRA), esppra.co.sz) -- supplier eligibility /
  suspension gate, evidence checklist
- Registrar of Companies (Ministry of Commerce, Industry and Trade) /
  Government Online Services (online.gov.sz) business/company
  registration
- Eswatini Revenue Service (ERS) Taxpayer Identification Number (TIN) /
  VAT registration

AGPL-3.0-or-later.

## Market-entry / statute catalogs

Governed public-sector market-entry compliance actor, same architecture
as every `cloud-itonami-iso3166-*` sibling in this fleet:

- `src/marketentry/{facts,governor,phase,sim,operation,registry,store,
  marketentryllm}.cljc` -- the actor. `facts.cljc` cites the Public
  Procurement Act No. 7 of 2011 (ESPPRA's own Government Gazette text,
  downloaded directly and read in full), ESPPRA itself ('officially
  established on the 27th May 2013'), the Registrar of Companies
  (Ministry of Commerce, Industry and Trade, named directly by the
  Eswatini Investment Promotion Authority's (EIPA) own text) and the
  Government Online Services registration portal, and the Eswatini
  Revenue Service (ERS, Revenue Authority Act No. 1 of 2008) Taxpayer
  Identification Number scheme. `governor.cljc`'s flagship check
  independently recomputes whether an engagement's own claimed supplier-
  eligibility self-certification survives the Act's own s.40(1)(g)/55/56
  suspension grounds (a discretionary ESPPRA suspension OR an automatic
  Register for Tender Defaulters endorsement) -- a disqualification-
  grounds OR-gate, a check shape genuinely different from every other
  iso3166 sibling's (see the namespace docstrings for the full research
  trail and honestly-narrowed scope, including facts this iteration
  could NOT verify, such as the detailed section text of the Companies
  Act/Employment Act -- both sit behind a Cloudflare bot-challenge on
  eswatinilii's individual document pages -- and the actual Regulations
  behind the Act's own s.39 Swazi-company-preference clause).
- `src/statute/facts.cljk` -- general-law catalog: the Companies Act,
  2009; the Employment Act, 1980; and the Value Added Tax Act, 2011
  (this last one grounded in a FULL primary-text read, not just a
  listing-table title -- a stronger citation than this family's LSO/NAM
  catalogs managed for their own jurisdictions' tax law).

Every citation is curl/WebFetch-verified against an official source
(esppra.co.sz, eswatinilii.org, gov.sz, online.gov.sz,
investeswatini.org.sz, ers.org.sz, centralbank.org.sz, sacu.int). The
Eswatini Legal Information Institute's own legislation-LISTING pages
loaded as ordinary server-rendered HTML, but EVERY individual document
page (where an Act's own detailed section text would be) returned a
Cloudflare 'Just a moment...' JS-challenge (HTTP 403) to `curl` this
iteration -- an honestly-flagged ACCESS gap, not a claim of
non-existence, see `marketentry.facts`'s and `statute.facts`'s
docstrings. This iteration also found and disclosed a genuine naming
variance across Eswatini's own official sources: the Eswatini
Investment Promotion Authority's own current-day text calls its
enabling law the 'Eswatini Investment Promotion Act of 1998', while the
Eswatini Legal Information Institute's own listing preserves the Act's
enactment-era title, 'Swaziland Investment Promotion Act, 1998' -- both
are reported as found, not resolved by guessing which is "more
correct".

Eswatini is a member of the Southern African Customs Union (SACU,
confirmed directly from sacu.int's own `/member-states/eswatini` page)
and its currency, the lilangeni (plural emalangeni, symbol E/L),
circulates through the Common Monetary Area at a one-to-one peg with
the South African rand -- confirmed directly from the Central Bank of
Eswatini's own currency page: 'The Lilangeni Currency (SZL) was
introduced in 1974 at par with the South African Rand through the CMA
(Common Monetary Area) to which it remains tied at a one-to-one
exchange rate.'

## Culture catalog

Alongside the market-entry / statute catalogs, this repo carries a
**country-level regional-culture catalog** (ADR-2607171400 addendum 2,
`cloud-itonami-municipality-culture-catalog` Wave 1, in
`com-junkawasaki/root`) — national dishes, protected products, beverages,
crafts, festivals and heritage sites for Eswatini:

- `src/culture/facts.cljk` — the catalog, source of truth (keyed by
  uppercase ISO3, mirroring `statute.facts`).
- `schema/culture.edn` — DataScript schema.
- `data/culture-tx.edn` — derived DataScript tx-data (regenerated from
  the catalog, never hand-edited).

City-level counterparts live in the `cloud-itonami-municipality-*` repos.
Same provenance discipline as the compliance catalogs: every entry cites a
source URL that was actually fetched and read on `:culture/retrieved-at`;
summaries state only what the cited source confirms. An item not in
`culture.facts/catalog` has no spec-basis — never fabricate one.
