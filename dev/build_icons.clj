(ns build-icons
  "Generate icons.edn from FontAwesome's icons.json"
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clojure.pprint :as pprint]))

(defn hex->unicode
  "Convert hex string to unicode character."
  [hex-str]
  (-> (Long/parseLong hex-str 16)
      char
      str))

(defn parse-icons-json
  "Parse FontAwesome icons.json and extract icon data.
   Only includes icons that are free (have the style in the 'free' array)."
  [json-path]
  (let [data (json/read-str (slurp json-path) :key-fn keyword)
        free-icon-entries (for [[icon-name {:keys [unicode label free styles]}] data
                                style-str styles
                                :let [free-styles (set free)]
                                :when (contains? free-styles style-str)]
                            [(keyword style-str) icon-name
                             {:unicode (hex->unicode unicode) :label label}])]
    (reduce
      (fn [acc [style-key icon-key icon-data]]
        (assoc-in acc [style-key icon-key] icon-data))
      {:solid {} :regular {} :brands {}}
      free-icon-entries)))

(defn generate-icons-edn!
  "Generate icons.edn from icons.json.
   
   Usage from command line:
   clj -X build-icons/generate-icons-edn!
   
   Or with custom paths:
   clj -X build-icons/generate-icons-edn! :icons-json-path '\"path/to/icons.json\"' :output-path '\"path/to/icons.edn\"'"
  [{:keys [icons-json-path output-path]
    :or {icons-json-path "metadata/icons.json"
         output-path "resources/clj-font-awesome/icons.edn"}}] 
  (let [icon-map (parse-icons-json icons-json-path)]
    (io/make-parents output-path)
    (spit output-path (with-out-str (pprint/pprint icon-map)))))

(comment
  (generate-icons-edn! {})
  
  (generate-icons-edn! {:icons-json-path "metadata/icons.json"
                        :output-path "resources/clj-font-awesome/icons.edn"}))
