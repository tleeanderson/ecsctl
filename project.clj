(defproject ecsctl "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.12.0"]
                 [com.cognitect.aws/api "0.8.735"]
                 [com.cognitect.aws/endpoints "871.2.30.22"]
                 [com.cognitect.aws/ecs "871.2.30.22"]]
  :repl-options {:init-ns ecsctl.core}
  :main ecsctl.core
  ;:skip-aot ecsctl.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
