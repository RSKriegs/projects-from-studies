from django.urls import include, path
from rest_framework import routers
from rest_framework.urlpatterns import format_suffix_patterns
from . import views

router = routers.DefaultRouter()
router.register(r'authors', views.AuthorViewSet,basename='authors')
router.register(r'quotes', views.QuoteViewSet,basename='quotes')
router.register(r'relatedquotes', views.RelatedAuthorViewSet,basename='relatedquotes')

urlpatterns = [
    path('', include(router.urls)),
    path('api-auth/', include('rest_framework.urls', namespace='rest_framework')),
]