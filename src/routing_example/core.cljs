(ns routing-example.core
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]

            [bidi.bidi :as bidi]
            ;;[schema.core :as s] ;For when redfining routes get tricky
            ;;[bidi.schema]

            [accountant.core :as accountant]))

(def app-routes
  ["/"
   [["" :index]
    ["section-a/"
     [["" :section-a]
      [["item/" :item-id] :section-a-item]]]
    ["section-b/" :section-b]
    [true :four-o-four]]])

;;(s/check bidi.schema/RoutePair app-routes)
;;(s/validate bidi.schema/RoutePair app-routes)

;; == Route components

(defn index []
  [:span
   [:h1 "Routing example: Index"]
   [:ul
    [:li [:a {:href (bidi/path-for app-routes :section-a) } "Section A"]]
    [:li [:a {:href (bidi/path-for app-routes :section-b) } "Section B"]]
    [:li [:a {:href "/borken/link" } "Borken link"]]]])

(defn section-a []
  [:span
   [:h1 "Routing example: Section A"]
   [:ul (map (fn [item-id]
               [:li {:key (str "item-" item-id)}
                [:a {:href (bidi/path-for app-routes :section-a-item :item-id item-id)} "Item: " item-id]])
             (range 1 6))]])

(defn a-item []
  [:span
   [:h1 (str "Routing example: Section A, item " (:item-id (session/get :route-params)))]
   [:p [:a {:href (bidi/path-for app-routes :section-a)} "Back to Section A"]]])

(defn section-b []
  [:span
   [:h1 "Routing example: Section B"]])

(defn four-o-four []
  [:span
   [:h1 "404"
    [:p "What you are looking for, "]
    [:p "I do not have."]]])

;; == /Route components

;; Map from route keywords to reagent components
(def page-components {:index {:component index}
                      :section-a {:component section-a}
                      :section-a-item {:component a-item}
                      :section-b {:component section-b}
                      :four-o-four {:component four-o-four}})

(defn page []
  [:div
   [:p [:a {:href (bidi/path-for app-routes :index) } "Go home"]]
   [:hr]
   [(:component ((session/get :current-page) page-components))] ;; <- This will "dispatch" a new "page" when  the session reagent/atom changes.
   ])


(defn on-js-reload []
  (reagent/render-component [page]
                            (. js/document (getElementById "app"))))

(defn ^:export init! []
  (accountant/configure-navigation!
   {:nav-handler (fn
                   [path]
                   (let [match (bidi/match-route app-routes path)
                         current-page (:handler match)
                         route-params (:route-params match)]
                     (session/put! :current-page current-page) ;; <- De-refed in the [page] component, triggering a "dispatch".
                     (session/put! :route-params route-params))) ;; To be de-refed by any component that needs them.
    :path-exists? (fn [path]
                    (boolean (bidi/match-route app-routes path)))})
  (accountant/dispatch-current!)
  (on-js-reload))
