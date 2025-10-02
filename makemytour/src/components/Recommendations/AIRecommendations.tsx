import React, { useState, useEffect } from 'react';
import { Button } from '../ui/button';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from '../ui/dialog';
import { Star, ThumbsUp, ThumbsDown, X, Sparkles, Info } from 'lucide-react';
import axios from 'axios';
import { API_BASE_URL } from '../../config/api';

interface Recommendation {
  id: string;
  userId: string;
  itemId: string;
  itemType: 'flight' | 'hotel';
  score: number;
  reason: string;
  tags: string[];
  createdAt: string;
  clicked: boolean;
  booked: boolean;
  feedback?: string;
}

interface AIRecommendationsProps {
  userId: string;
  onItemClick?: (itemId: string, itemType: string) => void;
}

const AIRecommendations: React.FC<AIRecommendationsProps> = ({ userId, onItemClick }) => {
  const [recommendations, setRecommendations] = useState<Recommendation[]>([]);
  const [loading, setLoading] = useState(false);
  const [showExplanation, setShowExplanation] = useState<string | null>(null);

  useEffect(() => {
    fetchRecommendations();
  }, [userId]);

  const fetchRecommendations = async () => {
    setLoading(true);
    try {
      const response = await axios.get(`${API_BASE_URL}/api/recommendations/${userId}`);
      setRecommendations(response.data);
    } catch (error) {
      console.error('Error fetching recommendations:', error);
    } finally {
      setLoading(false);
    }
  };

  const provideFeedback = async (recommendationId: string, feedback: 'helpful' | 'not-helpful' | 'irrelevant') => {
    try {
      await axios.post(`${API_BASE_URL}/api/recommendations/${recommendationId}/feedback`, {
        feedback
      });
      
      // Update local state
      setRecommendations(prev => 
        prev.map(rec => 
          rec.id === recommendationId 
            ? { ...rec, feedback }
            : rec
        )
      );
    } catch (error) {
      console.error('Error providing feedback:', error);
    }
  };

  const handleItemClick = (recommendation: Recommendation) => {
    if (onItemClick) {
      onItemClick(recommendation.itemId, recommendation.itemType);
    }
    
    // Mark as clicked (you might want to send this to backend)
    setRecommendations(prev => 
      prev.map(rec => 
        rec.id === recommendation.id 
          ? { ...rec, clicked: true }
          : rec
      )
    );
  };

  const getScoreColor = (score: number) => {
    if (score >= 0.8) return 'text-green-600 bg-green-100';
    if (score >= 0.6) return 'text-yellow-600 bg-yellow-100';
    return 'text-gray-600 bg-gray-100';
  };

  const getItemTypeIcon = (itemType: string) => {
    return itemType === 'flight' ? '✈️' : '🏨';
  };

  const RecommendationCard: React.FC<{ recommendation: Recommendation }> = ({ recommendation }) => (
    <div className="bg-white p-4 rounded-lg border hover:shadow-md transition-shadow">
      <div className="flex items-start justify-between mb-3">
        <div className="flex items-center gap-2">
          <span className="text-2xl">{getItemTypeIcon(recommendation.itemType)}</span>
          <div>
            <h3 className="font-semibold capitalize">
              {recommendation.itemType} Recommendation
            </h3>
            <div className={`inline-flex items-center gap-1 px-2 py-1 rounded-full text-xs font-medium ${getScoreColor(recommendation.score)}`}>
              <Star className="h-3 w-3" />
              {Math.round(recommendation.score * 100)}% Match
            </div>
          </div>
        </div>
        
        <Dialog open={showExplanation === recommendation.id} onOpenChange={(open) => setShowExplanation(open ? recommendation.id : null)}>
          <DialogTrigger asChild>
            <Button variant="ghost" size="sm">
              <Info className="h-4 w-4" />
            </Button>
          </DialogTrigger>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Why this recommendation?</DialogTitle>
            </DialogHeader>
            <div className="space-y-4">
              <p className="text-gray-700">{recommendation.reason}</p>
              
              <div>
                <h4 className="font-medium mb-2">Based on:</h4>
                <div className="flex flex-wrap gap-2">
                  {recommendation.tags.map((tag) => (
                    <span key={tag} className="bg-blue-100 text-blue-800 px-2 py-1 rounded text-sm">
                      {tag.replace('-', ' ')}
                    </span>
                  ))}
                </div>
              </div>
              
              <div className="text-sm text-gray-600">
                <p>Match Score: {Math.round(recommendation.score * 100)}%</p>
                <p>Generated: {new Date(recommendation.createdAt).toLocaleDateString()}</p>
              </div>
            </div>
          </DialogContent>
        </Dialog>
      </div>
      
      <p className="text-gray-700 mb-3">{recommendation.reason}</p>
      
      <div className="flex flex-wrap gap-1 mb-4">
        {recommendation.tags.slice(0, 3).map((tag) => (
          <span key={tag} className="bg-gray-100 text-gray-700 px-2 py-1 rounded text-xs">
            {tag.replace('-', ' ')}
          </span>
        ))}
        {recommendation.tags.length > 3 && (
          <span className="text-xs text-gray-500">+{recommendation.tags.length - 3} more</span>
        )}
      </div>
      
      <div className="flex items-center justify-between">
        <Button 
          onClick={() => handleItemClick(recommendation)}
          className="flex-1 mr-2"
        >
          View Details
        </Button>
        
        {!recommendation.feedback && (
          <div className="flex gap-1">
            <Button
              variant="ghost"
              size="sm"
              onClick={() => provideFeedback(recommendation.id, 'helpful')}
              title="Helpful"
            >
              <ThumbsUp className="h-4 w-4" />
            </Button>
            <Button
              variant="ghost"
              size="sm"
              onClick={() => provideFeedback(recommendation.id, 'not-helpful')}
              title="Not helpful"
            >
              <ThumbsDown className="h-4 w-4" />
            </Button>
            <Button
              variant="ghost"
              size="sm"
              onClick={() => provideFeedback(recommendation.id, 'irrelevant')}
              title="Not relevant"
            >
              <X className="h-4 w-4" />
            </Button>
          </div>
        )}
        
        {recommendation.feedback && (
          <div className="text-xs text-gray-500">
            Feedback: {recommendation.feedback}
          </div>
        )}
      </div>
    </div>
  );

  if (loading) {
    return (
      <div className="flex items-center justify-center p-8">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
        <span className="ml-2">Loading recommendations...</span>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center gap-2">
        <Sparkles className="h-6 w-6 text-blue-600" />
        <h2 className="text-xl font-semibold">AI Recommendations</h2>
        <span className="bg-blue-100 text-blue-800 px-2 py-1 rounded text-sm">
          Personalized for you
        </span>
      </div>
      
      {recommendations.length === 0 ? (
        <div className="text-center py-8">
          <Sparkles className="h-12 w-12 text-gray-400 mx-auto mb-4" />
          <h3 className="text-lg font-medium text-gray-600 mb-2">No recommendations yet</h3>
          <p className="text-gray-500">
            Start booking flights and hotels to get personalized recommendations!
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {recommendations.map((recommendation) => (
            <RecommendationCard key={recommendation.id} recommendation={recommendation} />
          ))}
        </div>
      )}
      
      <div className="bg-blue-50 p-4 rounded-lg">
        <h3 className="font-medium text-blue-800 mb-2">How it works</h3>
        <p className="text-sm text-blue-700">
          Our AI analyzes your booking history, preferences, and travel patterns to suggest 
          flights and hotels you'll love. The more you use our platform, the better our 
          recommendations become!
        </p>
      </div>
    </div>
  );
};

export default AIRecommendations;