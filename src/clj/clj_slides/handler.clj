(ns clj-slides.handler
  (:require
   [reitit.ring :as reitit-ring]
   [clj-slides.middleware :refer [middleware]]
   [hiccup.page :refer [include-js include-css html5]]
   [config.core :refer [env]]
   [clj-slides.parser :as parser]))

(def mount-target
  [:div#app
   [:h2 "Welcome to clj_slides"]
   [:p "please wait while Figwheel/shadow-cljs is waking up ..."]
   [:p "(Check the js console for hints if nothing exciting happens.)"]])

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-css (if (env :dev) "/css/site.css" "/css/site.min.css"))])

(defn loading-page []
  (html5
   (head)
   [:body {:class "body-container"}
    mount-target
    (include-js "/js/app.js")
    [:script "clj_slides.core.init_BANG_()"]]))

(defn index-handler
  [_request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (loading-page)})

(defn slide-handler-creator [slides-paginated]
  (fn [_request]
    {:status 200
     :headers {"Content-Type" "application/edn"}
     :body (pr-str slides-paginated)}))

(defn app-creator [slides-path]
  (let [slides-paginated (parser/get-paginated-slides slides-path)
        slide-handler (slide-handler-creator slides-paginated)]
    (reitit-ring/ring-handler
     (reitit-ring/router
      [["/" {:get {:handler index-handler}}]
       ["/items"
        ["" {:get {:handler index-handler}}]
        ["/:item-id" {:get {:handler index-handler
                            :parameters {:path {:item-id int?}}}}]]
       ["/about" {:get {:handler index-handler}}]
       ["/slides" {:get {:handler slide-handler}}]])
     (reitit-ring/routes
      (reitit-ring/create-resource-handler {:path "/" :root "/public"})
      (reitit-ring/create-default-handler))
     {:middleware middleware})))
