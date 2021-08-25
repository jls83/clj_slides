(ns clj-slides.handler
  (:require
   [reitit.ring :as reitit-ring]
   [clj-slides.middleware :refer [middleware]]
   [hiccup.page :refer [include-js include-css html5]]
   [commonmark-hiccup.core :refer [markdown->hiccup default-config]]
   [config.core :refer [env]]))

; TODO: Move this to a separate file
(defn parse-markdown-to-hiccup [raw-text]
  (markdown->hiccup default-config raw-text))

(defn paginate-hiccup [hiccup-seq]
  (remove #(= [:hr] (first %))
    (partition-by #(= [:hr] %) hiccup-seq)))

(def slide-file-path "blah.md")

(def slides-paginated
  (paginate-hiccup
    (parse-markdown-to-hiccup (slurp slide-file-path))))

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

(defn slide-handler
  [_request]
  {:status 200
   :headers {"Content-Type" "application/edn"}
   :body (pr-str slides-paginated)})

(def app
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
   {:middleware middleware}))
