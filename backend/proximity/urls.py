from django.urls import path
from . import views

urlpatterns = [
    path('', views.home, name='proximity-home'),
    path('about/', views.about, name='proximity-about'),
]
