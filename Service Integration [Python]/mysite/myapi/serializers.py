from rest_framework import serializers
from rest_framework.validators import UniqueTogetherValidator

from .models import Quote, Author

class QuoteSerializer(serializers.ModelSerializer):
    quote_url = serializers.HyperlinkedIdentityField(view_name='quotes-detail')
    author_url = serializers.HyperlinkedIdentityField(view_name='authors-detail')

    class Meta:
        model = Quote
        fields = ('id','quote_url','content','author','author_url','source','year','context')
        lookup_field = 'author'
        validators = [
            UniqueTogetherValidator(
                queryset=Quote.objects.all(),
                fields=['content', 'author'],
                message='A quote with the same content is already assigned to the same author'
            )
        ]

class AuthorSerializer(serializers.HyperlinkedModelSerializer):
    #quotes = QuoteSerializer(many=True, read_only=True)
    quotes = serializers.HyperlinkedRelatedField(many=True,view_name='quotes-detail',read_only=True)
    class Meta:
        model = Author
        fields = ('id','first_name','last_name','description','quotes')
        validators = [
            UniqueTogetherValidator(
                queryset=Author.objects.all(),
                fields=['first_name', 'last_name','description'],
                message = 'Author name should be unique - in case of duplicates, provide a description'
            )
        ]