(ns clj-font-awesome.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [clj-font-awesome.core :as core]))

(deftest icons-test
  (testing "icons returns a map for each style"
    (is (map? (core/icons)))
    (is (map? (core/icons :solid)))
    (is (map? (core/icons :regular)))
    (is (map? (core/icons :brands)))))

(deftest all-icons-test
  (testing "all-icons returns a sequence of keywords"
    (let [solid-icons (core/all-icons :solid)]
      (is (seq solid-icons))
      (is (every? keyword? solid-icons))))
  
  (testing "default style is solid"
    (is (= (core/all-icons) (core/all-icons :solid)))))

(deftest unicode-test
  (testing "unicode returns string for valid icons"
    (is (string? (core/unicode :gear)))
    (is (string? (core/unicode :heart :regular)))
    (is (string? (core/unicode :github :brands))))
  
  (testing "unicode returns nil for invalid icons"
    (is (nil? (core/unicode :nonexistent-icon-12345)))
    (is (nil? (core/unicode :gear :brands)))) ; gear is not a brand icon
  
  (testing "default style is solid"
    (is (= (core/unicode :gear) (core/unicode :gear :solid)))))

(deftest label-test
  (testing "label returns string for valid icons"
    (is (string? (core/label :gear))))
  
  (testing "label returns nil for invalid icons"
    (is (nil? (core/label :nonexistent-icon-12345)))))

(deftest icon-exists?-test
  (testing "icon-exists? returns true for valid icons"
    (is (core/icon-exists? :gear))
    (is (core/icon-exists? :github :brands)))
  
  (testing "icon-exists? returns false for invalid icons"
    (is (not (core/icon-exists? :nonexistent-icon-12345)))
    (is (not (core/icon-exists? :github :solid))))) ; github is brands only

(deftest unicode!-test
  (testing "unicode! returns unicode for valid icons"
    (is (= (core/unicode :gear) (core/unicode! :gear))))
  
  (testing "unicode! returns fallback for invalid icons"
    (is (= core/fallback-unicode (core/unicode! :nonexistent-icon-12345)))))

(deftest icon-data-test
  (testing "icon-data returns map with unicode and label"
    (let [data (core/icon-data :gear)]
      (is (map? data))
      (is (contains? data :unicode))
      (is (contains? data :label))))
  
  (testing "icon-data returns nil for invalid icons"
    (is (nil? (core/icon-data :nonexistent-icon-12345)))))

(deftest search-icons-test
  (testing "search-icons finds matching icons"
    (let [results (core/search-icons "heart")]
      (is (seq results))
      (is (some #(= % :heart) results))))
  
  (testing "search-icons returns empty for no matches"
    (is (empty? (core/search-icons "xyznonexistent12345")))))

(deftest icon-count-test
  (testing "icon-count returns positive number"
    (is (pos? (core/icon-count)))
    (is (pos? (core/icon-count :solid)))
    (is (pos? (core/icon-count :regular)))
    (is (pos? (core/icon-count :brands))))
  
  (testing "solid has most icons"
    (is (> (core/icon-count :solid) (core/icon-count :regular)))))

(deftest well-known-icons-test
  (testing "common solid icons exist"
    (is (core/icon-exists? :gear :solid))
    (is (core/icon-exists? :house :solid))  ; FA7 uses :house instead of :home
    (is (core/icon-exists? :user :solid))
    (is (core/icon-exists? :check :solid))
    (is (core/icon-exists? :xmark :solid))
    (is (core/icon-exists? :plus :solid))
    (is (core/icon-exists? :minus :solid)))
  
  (testing "common brand icons exist"
    (is (core/icon-exists? :github :brands))
    (is (core/icon-exists? :apple :brands))
    (is (core/icon-exists? :windows :brands))
    (is (core/icon-exists? :linux :brands))))
