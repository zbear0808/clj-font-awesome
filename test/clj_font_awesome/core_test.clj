(ns clj-font-awesome.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [clj-font-awesome.core :as core]))

(deftest unicode!-valid-icon-test
  (testing "known icon returns its unicode"
    (let [result (core/unicode! :fire :solid)]
      (is (string? result))
      (is (not= result core/fallback-unicode)
          ":fire should return valid unicode, not fallback"))))

(deftest unicode!-invalid-icon-test
  (testing "unknown icon returns fallback unicode"
    (let [result (core/unicode! :this-icon-definitely-does-not-exist :solid)]
      (is (= result core/fallback-unicode)
          "invalid icon should return fallback-unicode"))))
