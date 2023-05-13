(def csv-data
  (with-open [reader (io/reader "hh.csv")]
    (doall (csv/read-csv reader))))

(def headers (first csv-data))
(def products (map #(zipmap headers %) (rest csv-data)))

(defn product [s]
  (filter #(= s (get % "name")) products))

(comment
  (product "Tie Dye Tee")
  )