(def csv-data
  (with-open [reader (io/reader "hh.csv")]
    ;; Babashka aliases clojure.data.csv as csv
    (doall (csv/read-csv reader))))

csv-data