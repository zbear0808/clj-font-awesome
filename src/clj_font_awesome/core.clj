(ns clj-font-awesome.core
  "FontAwesome 7 icon library for JavaFX/cljfx applications.
   
   This namespace provides functions for looking up icon unicode characters
   by name and style. Icons are lazy-loaded from the bundled icons.edn resource.
   
   Basic usage:
     (unicode :gear)           ; => \"\\uf013\"
     (unicode :heart :regular) ; => \"\\uf004\"
     (unicode :github :brands) ; => \"\\uf09b\"
   
   Available styles:
     :solid   - Filled icons (default, most icons available)
     :regular - Outlined icons (subset of solid)
     :brands  - Brand/logo icons (e.g. GitHub, Twitter)"
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]))

(def ^:private icon-map
  "Icon definitions loaded from EDN resource.
   Lazy-loaded on first access to avoid startup overhead."
  (delay
    (-> (io/resource "clj-font-awesome/icons.edn")
        slurp
        edn/read-string)))

(defn icons
  "Get all icons for a style. 
   Returns map of {icon-keyword {:unicode \"...\" :label \"...\"}}
   
   Examples:
     (icons)        ; => all solid icons
     (icons :solid) ; => all solid icons
     (icons :brands); => all brand icons"
  ([] (icons :solid))
  ([style] (get @icon-map style)))

(defn all-icons
  "Get all icon names for a style as keywords.
   
   Examples:
     (all-icons)         ; => (:gear :heart :home ...)
     (all-icons :brands) ; => (:github :twitter ...)"
  ([] (all-icons :solid))
  ([style] (keys (icons style))))

(defn unicode
  "Get unicode character for an icon.
   Returns nil if icon not found in the specified style.
   
   Examples:
     (unicode :gear)           ; => \"\\uf013\"
     (unicode :heart :regular) ; => \"\\uf004\"
     (unicode :github :brands) ; => \"\\uf09b\"
     (unicode :nonexistent)    ; => nil"
  ([icon-name] (unicode icon-name :solid))
  ([icon-name style]
   (get-in @icon-map [style icon-name :unicode])))

(defn label
  "Get human-readable label for an icon.
   Returns nil if icon not found.
   
   Examples:
     (label :gear)           ; => \"Gear\"
     (label :heart :regular) ; => \"Heart\""
  ([icon-name] (label icon-name :solid))
  ([icon-name style]
   (get-in @icon-map [style icon-name :label])))

(defn icon-exists?
  "Check if an icon exists in the given style.
   
   Examples:
     (icon-exists? :gear)            ; => true
     (icon-exists? :nonexistent)     ; => false
     (icon-exists? :github :brands)  ; => true
     (icon-exists? :github :solid)   ; => false"
  ([icon-name] (icon-exists? icon-name :solid))
  ([icon-name style]
   (contains? (icons style) icon-name)))

(def fallback-unicode
  "Unicode for circle-question icon (used when icon not found).
   Can be used as a fallback to indicate missing icons."
  "\uf059")

(defn unicode!
  "Get unicode character for an icon, with fallback.
   Returns fallback-unicode if icon not found.
   
   Examples:
     (unicode! :gear)        ; => \"\\uf013\"
     (unicode! :nonexistent) ; => \"\\uf059\" (circle-question)"
  ([icon-name] (unicode! icon-name :solid))
  ([icon-name style]
   (or (unicode icon-name style) fallback-unicode)))

(defn icon-data
  "Get full icon data map including unicode and label.
   Returns nil if icon not found.
   
   Examples:
     (icon-data :gear) ; => {:unicode \"\\uf013\" :label \"Gear\"}"
  ([icon-name] (icon-data icon-name :solid))
  ([icon-name style]
   (get-in @icon-map [style icon-name])))

(defn search-icons
  "Search for icons by name substring.
   Returns a sequence of matching icon keywords.
   
   Examples:
     (search-icons \"heart\")          ; => (:heart :heart-circle-bolt ...)
     (search-icons \"arrow\" :solid)   ; => (:arrow-up :arrow-down ...)"
  ([query] (search-icons query :solid))
  ([query style]
   (let [q (clojure.string/lower-case query)]
     (filterv #(clojure.string/includes? (name %) q) (all-icons style)))))

(defn icon-count
  "Get the number of icons for a style.
   
   Examples:
     (icon-count)        ; => 2000+ (solid icons)
     (icon-count :brands); => 400+ (brand icons)"
  ([] (icon-count :solid))
  ([style] (count (icons style))))
