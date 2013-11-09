(defproject me.vedang/bloomclj "0.1.0-SNAPSHOT"
  :description "A Bloom Filter implementation in Clojure."
  :url "http://github.com/vedang/bloomclj"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :global-vars {*warn-on-reflection* true}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [com.taoensso/carmine "2.2.0"]
                 [com.google.guava/guava "15.0"]
                 [clj-time "0.6.0"]])
