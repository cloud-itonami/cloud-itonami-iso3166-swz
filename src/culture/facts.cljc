(ns culture.facts
  "Country-level regional-culture catalog for Eswatini (SWZ) -- national
  dishes, protected products, beverages, crafts, festivals and heritage
  sites, per ADR-2607171400 addendum 2 (cloud-itonami-municipality-
  culture-catalog Wave 1, in com-junkawasaki/root). Sibling namespace to
  `marketentry.facts` / `statute.facts` (ADR-2607141700); city-level
  counterparts live in the cloud-itonami-municipality-* repos.

  Catalog is keyed by UPPERCASE ISO3 (mirrors `statute.facts`); entries
  carry no :culture/municipality (that attribute is city-level only).

  Every entry cites a source URL that was actually fetched and read on
  :culture/retrieved-at -- never fabricated. Summaries state only what the
  cited source confirms. An item not in this table has NO spec-basis, full
  stop; extend `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of culture entries."
  {"SWZ"
   [{:culture/id "swz.dish.sishwala"
     :culture/name "Sishwala"
     :culture/country "SWZ"
     :culture/kind :dish
     :culture/summary "Thick Swazi maize-meal porridge normally served with meat or vegetables."
     :culture/url "https://en.wikipedia.org/wiki/Cuisine_of_Eswatini"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "swz.dish.incwancwa"
     :culture/name "Incwancwa"
     :culture/country "SWZ"
     :culture/kind :dish
     :culture/summary "Sour Swazi porridge made of fermented cornmeal."
     :culture/url "https://en.wikipedia.org/wiki/Cuisine_of_Eswatini"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "swz.dish.sidvudvu"
     :culture/name "Sidvudvu"
     :culture/country "SWZ"
     :culture/kind :dish
     :culture/summary "Swazi porridge made of pumpkin mixed with cornmeal."
     :culture/url "https://en.wikipedia.org/wiki/Cuisine_of_Eswatini"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "swz.beverage.umcombotsi"
     :culture/name "Umcombotsi"
     :culture/name-local "Tjwala"
     :culture/country "SWZ"
     :culture/kind :beverage
     :culture/summary "Traditional brewed beer in Siswati, also called tjwala."
     :culture/url "https://en.wikipedia.org/wiki/Cuisine_of_Eswatini"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "swz.craft.grasswork"
     :culture/name "Emacansi grasswork"
     :culture/country "SWZ"
     :culture/kind :craft
     :culture/summary "Traditional Swazi grasswork craft producing mats (emacansi, tihlantsi), brooms and baskets."
     :culture/url "https://en.wikipedia.org/wiki/Culture_of_Eswatini"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "swz.festival.incwala"
     :culture/name "Incwala"
     :culture/country "SWZ"
     :culture/kind :festival
     :culture/summary "The main ritual of kingship in the Kingdom of Eswatini, a national month-long ceremony held around the summer solstice involving the king, priests, warriors and citizens."
     :culture/url "https://en.wikipedia.org/wiki/Incwala"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "swz.festival.umhlanga"
     :culture/name "Umhlanga"
     :culture/name-local "Reed Dance"
     :culture/country "SWZ"
     :culture/kind :festival
     :culture/summary "Eight-day annual ceremony held at the end of August or beginning of September at Ludzidzini Royal Village in Eswatini, where tens of thousands of unmarried and childless Swazi girls and women cut reeds and dance for the Queen Mother."
     :culture/url "https://en.wikipedia.org/wiki/Umhlanga_(ceremony)"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "swz.heritage.ngwenya-mine"
     :culture/name "Ngwenya Mine"
     :culture/country "SWZ"
     :culture/kind :heritage
     :culture/summary "Mine on Bomvu Ridge in northwestern Eswatini regarded as the world's oldest, with evidence of mining activity dating back 41,000-43,000 years; a 2008 submission to the UNESCO World Heritage list was not accepted."
     :culture/url "https://en.wikipedia.org/wiki/Ngwenya_Mine"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}]})

(defn spec-basis [iso3] (get catalog iso3))

(defn coverage
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-swz culture catalog "
                 "(ADR-2607171400 addendum 2, Wave 1): " (count (get catalog "SWZ"))
                 " SWZ entries, each with a fetched-and-read citation. "
                 "Extend `culture.facts/catalog`, never fabricate an id/url.")})))

(defn by-kind [iso3 kind]
  (filterv #(= (:culture/kind %) kind) (spec-basis iso3)))
