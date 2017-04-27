(ns guestbook.core
  (:require [reagent.core :as r :refer [atom]]
            [ajax.core :refer [GET POST]]))

(defn send-message! [fields errors]
  (POST "/add-message"
        {:format :json
         :headers
         {"Accept" "application/transit+json"
          "x-csrf-token" (.-value (.getElementById js/document "token"))}
         :params @fields
         :handler #(do
                     (.log js/console (str "response:" %))
                     (reset! errors nil))
         :error-handler #(do
                           (.log js/console (str %))
                           (reset! errors (get-in % [:response :errors])))}))

(defn errors-component [errors id]
  (when-let [error (id @errors)]
    [:div.alert.alert-danger (clojure.string/join error)]))

(defn message-form []
  (let [fields (atom {})
        errors (atom nil)]
    (fn []
      [:div.content
       [errors-component errors :server-error]
       [:div.form-group
        [errors-component errors :name]
        [:p "Name:"
         [:input.form-control
          {:type :text
           :name :name
           :on-change #(swap! fields assoc :name (-> % .-target .-value))
           :value (:name @fields)}]]
        [errors-component errors :message]
        [:p "Message:"
         [:textarea.form-control
          {:rows 4
           :cols 50
           :name :message
           :value (:message @fields)
           :on-change #(swap! fields assoc :message (-> % .-target .-value))}]]
        [:input.btn.btn-primary
         {:type :submit
          :on-click #(send-message! fields errors)
          :value "comment"}]]])))


(defn home []
  [:div.row
   [:div.span12
    [message-form]]])

(r/render
 [home]
 (.getElementById js/document "content"))
