(def csv-data
  (with-open [reader (io/reader "hh.csv")]
    (doall (csv/read-csv reader))))

(def headers (first csv-data))
(def products (map #(zipmap headers %) (rest csv-data)))

(defn product [s]
  (filter #(= s (get % "name")) products))

(comment
  (map #(get % "retail_price") (product "Tie Dye Tee"))
  )

(for [i (range (count products))]
  {:n i 
   :name (get (nth products i) "name")
   :price (get (nth products i) "retail_price")
   :variant (get (nth products i) "variant_option_one_value")})