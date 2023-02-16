from rest_framework import viewsets
from rest_framework import generics
from rest_framework.exceptions import NotFound
from rest_framework.response import Response
from rest_framework.pagination import PageNumberPagination
from rest_framework import status
from collections import OrderedDict

from .serializers import QuoteSerializer, AuthorSerializer
from .models import Quote, Author

#HATEOAS
class LinksAwarePageNumberPagination(PageNumberPagination):
   def get_paginated_response(self, data, links=[]):
       return Response(OrderedDict([
          ('count', self.page.paginator.count),
          ('next', self.get_next_link()),
          ('previous', self.get_previous_link()),
          ('results', data),
          ('_links', links),
       ]))

class HateoasModelViewSet(viewsets.ModelViewSet):
    """
    This class should be inherited by viewsets that wants to provide hateoas links
    You should override following methodes:
      - get_list_links
      - get_retrieve_links
      - get_create_links
      - get_update_links
      - get_destroy_links
    """

    pagination_class = LinksAwarePageNumberPagination


    def get_list_links(self, request):
        return {}

    def get_retrieve_links(self, request, instance):
        return {}

    def get_create_links(self, request):
        return {}

    def get_update_links(self, request, instance):
        return {}

    def get_destroy_links(self, request, instance):
        return {}

    def get_paginated_response(self, data, links=None):
        assert self.paginator is not None
        return self.paginator.get_paginated_response(data, links)

    def list(self, request, *args, **kwargs):
        queryset = self.filter_queryset(self.get_queryset())

        page = self.paginate_queryset(queryset)
        if page is not None:
            serializer = self.get_serializer(page, many=True)
            return self.get_paginated_response(serializer.data, links=self.get_list_links())

        serializer = self.get_serializer(queryset, many=True)

        return Response(OrderedDict([
            ('results', serializer.data),
            ('_links', self.get_list_links(request))
        ]))

    def retrieve(self, request, *args, **kwargs):
        instance = self.get_object()
        serializer = self.get_serializer(instance)
        data = serializer.data
        data['_links'] = self.get_retrieve_links(request, instance)
        return Response(data)

    def create(self, request, *args, **kwargs):
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        self.perform_create(serializer)
        headers = self.get_success_headers(serializer.data)
        data = serializer.data
        data['_links'] = self.get_create_links(request)
        return Response(serializer.data, status=status.HTTP_201_CREATED, headers=headers)

    def update(self, request, *args, **kwargs):
        partial = kwargs.pop('partial', False)
        instance = self.get_object()
        serializer = self.get_serializer(instance, data=request.data, partial=partial)
        serializer.is_valid(raise_exception=True)
        self.perform_update(serializer)
        data = serializer.data
        data['_links'] = self.get_update_links(request, instance)
        return Response(serializer.data)

    def destroy(self, request, *args, **kwargs):
        instance = self.get_object()
        data = {'_links': self.get_destroy_links(request, instance)}
        self.perform_destroy(instance)
        return Response(data,status=status.HTTP_204_NO_CONTENT)
#########################################################################################

class AuthorViewSet(HateoasModelViewSet):
    http_method_names = ['get', 'put', 'post','patch', 'delete']
    serializer_class = AuthorSerializer

    try:
        Author.objects.get_or_create(first_name='', last_name='', description='Dummy')
    except:
        pass

    def get_queryset(self):
        queryset = Author.objects.all().order_by('id')
        id = self.request.query_params.get('id')
        first_name = self.request.query_params.get('first_name')
        last_name = self.request.query_params.get('last_name')
        if id is not None:
            queryset = queryset.filter(id=id)
        if first_name is not None:
            queryset = queryset.filter(first_name=first_name)
        if last_name is not None:
            queryset = queryset.filter(last_name=last_name)
        if queryset:
            return queryset
        else:
            raise NotFound

    def get_list_links(self, request):
        return {
            'self': {'href': request.build_absolute_uri()},
            'quotes': {'href': request.build_absolute_uri('/quotes/')},
            'relatedquotes': {'href': request.build_absolute_uri('/relatedquotes/')}
        }

class QuoteViewSet(HateoasModelViewSet):
    http_method_names = ['get', 'put', 'post','patch', 'delete']
    serializer_class = QuoteSerializer

    def get_queryset(self):
        queryset = Quote.objects.all()
        id = self.request.query_params.get('id')
        author = self.request.query_params.get('author')
        source = self.request.query_params.get('source')
        year = self.request.query_params.get('year')
        if id is not None:
            queryset = queryset.filter(id=id)
        if author is not None:
            queryset = queryset.filter(author=author)
        if source is not None:
            queryset = queryset.filter(source=source)
        if year is not None:
            queryset = queryset.filter(year=year)
        if queryset:
            return queryset
        else:
            raise NotFound

    def get_list_links(self, request):
        return {
            'self': {'href': request.build_absolute_uri()},
            'authors': {'href': request.build_absolute_uri('/authors/')},
        }

class RelatedAuthorViewSet(HateoasModelViewSet):
    http_method_names = ['get','patch', 'delete']
    serializer_class = QuoteSerializer
    #lookup_field = 'author'

    def get_queryset(self):
        id = self.request.query_params.get('id')
        queryset = Quote.objects.filter(author_id=id).exclude(author_id__isnull=True)
        if queryset:
            return queryset
        else:
            raise NotFound

    def get_list_links(self, request):
        return {
            'self': {'href': request.build_absolute_uri()},
            'quotes': {'href': request.build_absolute_uri('/quotes/')},
            'authors': {'href': request.build_absolute_uri('/authors/')}
        }
