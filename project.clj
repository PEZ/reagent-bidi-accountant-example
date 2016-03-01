(defproject routing-example "0.1.0-SNAPSHOT"
  :description "Client side routing with bidi and accountant"
  :url "https://github.com/PEZ/routing-example"

  :min-lein-version "2.5.3"
  
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]
                 [org.clojure/core.async "0.2.374"
                  :exclusions [org.clojure/tools.reader]]
                 [reagent "0.5.1"]
                 [reagent-utils "0.1.7"]
                 [bidi "2.0.0"]
                 [venantius/accountant "0.1.7"]]
  
  :plugins [[lein-figwheel "0.5.0-6"]
            [lein-cljsbuild "1.1.2" :exclusions [[org.clojure/clojure]]]]

  :source-paths ["src"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :profiles {:dev
             {:source-paths ["dev"]
              :dependencies [[figwheel-sidecar "0.5.0-6" :exclusions [org.clojure/clojure]]
                             [com.cemerick/piggieback "0.2.1" :exclusions [org.clojure/clojure]]
                             [prismatic/schema "1.0.5"]]}}


  :cljsbuild {:builds
              {:dev
               {:source-paths ["src"]

                :figwheel {:on-jsload "routing-example.core/on-js-reload"}

                :compiler {:main routing-example.core
                           :asset-path "/js/compiled/out"
                           :output-to "resources/public/js/compiled/routing_example.js"
                           :output-dir "resources/public/js/compiled/out"
                           :source-map-timestamp true}}
               
               ;; This next build is an compressed minified build for
               ;; production. You can build this with:
               ;; lein cljsbuild once min
               :min
               {:source-paths ["src"]
                :compiler {:output-to "resources/public/js/compiled/routing_example.js"
                           :main routing-example.core
                           :optimizations :advanced
                           :pretty-print false}}}}

  :figwheel {:http-server-root "public"
             :server-port 3449
             :server-ip "0.0.0.0"
             :websocket-host :js-client-host
             :css-dirs ["resources/public/css"]
             :ring-handler routing-example.server/handler}
  
  :repl-options {:init-ns routing-example.user
                 :skip-default-init false
                 :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]})
