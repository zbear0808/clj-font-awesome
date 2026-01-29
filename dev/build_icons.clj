(ns build-icons
  "Generate icons.edn from FontAwesome's icons.json"
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clojure.pprint :as pprint]
            [clojure.string :as str]))

(defn hex->unicode
  "Convert hex string to unicode character."
  [hex-str]
  (-> (Long/parseLong hex-str 16)
      char
      str))

(defn icon-name->keyword
  "Convert icon name to idiomatic Clojure keyword."
  [name]
  (keyword name))

(defn parse-icons-json
  "Parse FontAwesome icons.json and extract icon data.
   Only includes icons that are free (have the style in the 'free' array)."
  [json-path]
  (let [data (json/read-str (slurp json-path) :key-fn keyword)]
    (reduce-kv
      (fn [acc icon-name info]
        (let [unicode-char (hex->unicode (:unicode info))
              label (:label info)
              free-styles (set (:free info))]
          (reduce
            (fn [acc style-str]
              (let [style-key (keyword style-str)]
                (if (contains? free-styles style-str)
                  (assoc-in acc [style-key (icon-name->keyword (name icon-name))]
                            {:unicode unicode-char :label label})
                  acc)))
            acc
            (:styles info))))
      {:solid {} :regular {} :brands {}}
      data)))

(defn generate-icons-edn!
  "Generate icons.edn from icons.json.
   
   Usage from command line:
   clj -X build-icons/generate-icons-edn!
   
   Or with custom paths:
   clj -X build-icons/generate-icons-edn! :icons-json-path '\"path/to/icons.json\"' :output-path '\"path/to/icons.edn\"'"
  [{:keys [icons-json-path output-path]
    :or {icons-json-path "metadata/icons.json"
         output-path "resources/clj-font-awesome/icons.edn"}}]
  (println "Parsing icons from:" icons-json-path)
  (let [icon-map (parse-icons-json icons-json-path)
        solid-count (count (:solid icon-map))
        regular-count (count (:regular icon-map))
        brands-count (count (:brands icon-map))
        total-count (+ solid-count regular-count brands-count)]
    (io/make-parents output-path)
    (spit output-path (with-out-str (pprint/pprint icon-map)))
    (println "Generated" output-path)
    (println "  Solid icons:" solid-count)
    (println "  Regular icons:" regular-count)
    (println "  Brand icons:" brands-count)
    (println "  Total:" total-count)
    {:solid-count solid-count
     :regular-count regular-count
     :brands-count brands-count
     :total-count total-count}))

(comment
  ;; For REPL usage:
  (generate-icons-edn! {})
  
  ;; With custom paths:
  (generate-icons-edn! {:icons-json-path "metadata/icons.json"
                        :output-path "resources/clj-font-awesome/icons.edn"}))
