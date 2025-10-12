import React, { useState, useEffect } from 'react';
import { Star, ThumbsUp, Flag, Camera, MessageSquare } from 'lucide-react';
import { Button } from '../ui/button';
import { Textarea } from '../ui/textarea';
import { Input } from '../ui/input';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from '../ui/dialog';
import { SimpleSelect } from '../ui/simple-select';
import axios from 'axios';
import { API_BASE_URL } from '../../config/api';

interface Review {
  id: string;
  userId: string;
  userName: string;
  rating: number;
  title: string;
  comment: string;
  photos: string[];
  reviewDate: string;
  helpfulCount: number;
  helpfulUsers: string[];
  verified: boolean;
  reply?: string;
  replyDate?: string;
}

interface ReviewSystemProps {
  itemId: string;
  itemType: 'flight' | 'hotel';
  currentUserId?: string;
}

const ReviewSystem: React.FC<ReviewSystemProps> = ({ itemId, itemType, currentUserId }) => {
  const [reviews, setReviews] = useState<Review[]>([]);
  const [sortBy, setSortBy] = useState('recent');
  const [showWriteReview, setShowWriteReview] = useState(false);
  const [newReview, setNewReview] = useState({
    rating: 0,
    title: '',
    comment: '',
    photos: [] as string[]
  });
  const [averageRating, setAverageRating] = useState(0);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchReviews();
    fetchAverageRating();
  }, [itemId, itemType, sortBy]);

  const fetchReviews = async () => {
    try {
      const response = await axios.get(
        `${API_BASE_URL}/api/reviews/${itemId}/${itemType}?sortBy=${sortBy}`
      );
      setReviews(response.data);
    } catch (error) {
      console.error('Error fetching reviews:', error);
    }
  };

  const fetchAverageRating = async () => {
    try {
      const response = await axios.get(
        `${API_BASE_URL}/api/reviews/${itemId}/${itemType}/average-rating`
      );
      setAverageRating(response.data.averageRating);
    } catch (error) {
      console.error('Error fetching average rating:', error);
    }
  };

  const submitReview = async () => {
    if (!currentUserId || newReview.rating === 0) return;

    setLoading(true);
    try {
      const reviewData = {
        ...newReview,
        userId: currentUserId,
        userName: 'Current User', // In real app, get from user context
        itemId,
        itemType
      };

      await axios.post(`${API_BASE_URL}/api/reviews`, reviewData);
      setShowWriteReview(false);
      setNewReview({ rating: 0, title: '', comment: '', photos: [] });
      fetchReviews();
      fetchAverageRating();
    } catch (error) {
      console.error('Error submitting review:', error);
    } finally {
      setLoading(false);
    }
  };

  const markHelpful = async (reviewId: string) => {
    if (!currentUserId) return;

    try {
      await axios.post(`${API_BASE_URL}/api/reviews/${reviewId}/helpful`, {
        userId: currentUserId
      });
      fetchReviews();
    } catch (error) {
      console.error('Error marking review as helpful:', error);
    }
  };

  const flagReview = async (reviewId: string, reason: string) => {
    try {
      await axios.post(`${API_BASE_URL}/api/reviews/${reviewId}/flag`, {
        reason
      });
      fetchReviews();
    } catch (error) {
      console.error('Error flagging review:', error);
    }
  };

  const renderStars = (rating: number, interactive = false, onRate?: (rating: number) => void) => {
    return (
      <div className="flex gap-1">
        {[1, 2, 3, 4, 5].map((star) => (
          <Star
            key={star}
            className={`h-5 w-5 ${
              star <= rating ? 'fill-yellow-400 text-yellow-400' : 'text-gray-300'
            } ${interactive ? 'cursor-pointer hover:text-yellow-400' : ''}`}
            onClick={() => interactive && onRate && onRate(star)}
          />
        ))}
      </div>
    );
  };

  return (
    <div className="space-y-6">
      {/* Header with average rating and write review button */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <div className="text-center">
            <div className="text-3xl font-bold">{averageRating.toFixed(1)}</div>
            {renderStars(Math.round(averageRating))}
            <div className="text-sm text-gray-600">{reviews.length} reviews</div>
          </div>
        </div>
        
        {currentUserId && (
          <Dialog open={showWriteReview} onOpenChange={setShowWriteReview}>
            <DialogTrigger asChild>
              <Button>Write a Review</Button>
            </DialogTrigger>
            <DialogContent className="sm:max-w-lg">
              <DialogHeader>
                <DialogTitle>Write a Review</DialogTitle>
              </DialogHeader>
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium mb-2">Rating</label>
                  {renderStars(newReview.rating, true, (rating) => 
                    setNewReview(prev => ({ ...prev, rating }))
                  )}
                </div>
                
                <div>
                  <label className="block text-sm font-medium mb-2">Title</label>
                  <Input
                    type="text"
                    value={newReview.title}
                    onChange={(e) => setNewReview(prev => ({ ...prev, title: e.target.value }))}
                    placeholder="Summarize your experience"
                  />
                </div>
                
                <div>
                  <label className="block text-sm font-medium mb-2">Review</label>
                  <Textarea
                    value={newReview.comment}
                    onChange={(e) => setNewReview(prev => ({ ...prev, comment: e.target.value }))}
                    placeholder="Share your experience..."
                    rows={4}
                  />
                </div>
                
                <div className="flex gap-2">
                  <Button variant="outline" onClick={() => setShowWriteReview(false)}>
                    Cancel
                  </Button>
                  <Button 
                    onClick={submitReview} 
                    disabled={loading || newReview.rating === 0}
                  >
                    {loading ? 'Submitting...' : 'Submit Review'}
                  </Button>
                </div>
              </div>
            </DialogContent>
          </Dialog>
        )}
      </div>

      {/* Sort options */}
      <div className="flex items-center gap-4">
        <span className="text-sm font-medium">Sort by:</span>
        <SimpleSelect 
          value={sortBy} 
          onValueChange={setSortBy}
          options={[
            { value: "recent", label: "Most Recent" },
            { value: "helpful", label: "Most Helpful" },
            { value: "rating", label: "Highest Rating" }
          ]}
          className="w-40"
        />
      </div>

      {/* Reviews list */}
      <div className="space-y-4">
        {reviews.map((review) => (
          <div key={review.id} className="border rounded-lg p-4 space-y-3">
            <div className="flex items-start justify-between">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-blue-500 rounded-full flex items-center justify-center text-white font-semibold">
                  {review.userName.charAt(0)}
                </div>
                <div>
                  <div className="flex items-center gap-2">
                    <span className="font-medium">{review.userName}</span>
                    {review.verified && (
                      <span className="bg-green-100 text-green-800 text-xs px-2 py-1 rounded">
                        Verified
                      </span>
                    )}
                  </div>
                  <div className="flex items-center gap-2">
                    {renderStars(review.rating)}
                    <span className="text-sm text-gray-600">
                      {new Date(review.reviewDate).toLocaleDateString()}
                    </span>
                  </div>
                </div>
              </div>
              
              <Dialog>
                <DialogTrigger asChild>
                  <Button variant="ghost" size="sm">
                    <Flag className="h-4 w-4" />
                  </Button>
                </DialogTrigger>
                <DialogContent>
                  <DialogHeader>
                    <DialogTitle>Report Review</DialogTitle>
                  </DialogHeader>
                  <div className="space-y-4">
                    <SimpleSelect 
                      value=""
                      onValueChange={(reason: string) => flagReview(review.id, reason)}
                      placeholder="Select reason"
                      options={[
                        { value: "inappropriate", label: "Inappropriate content" },
                        { value: "spam", label: "Spam" },
                        { value: "fake", label: "Fake review" },
                        { value: "offensive", label: "Offensive language" }
                      ]}
                    />
                  </div>
                </DialogContent>
              </Dialog>
            </div>
            
            <div>
              <h4 className="font-medium mb-1">{review.title}</h4>
              <p className="text-gray-700">{review.comment}</p>
            </div>
            
            {review.photos.length > 0 && (
              <div className="flex gap-2">
                {review.photos.map((photo, index) => (
                  <img
                    key={index}
                    src={photo}
                    alt={`Review photo ${index + 1}`}
                    className="w-20 h-20 object-cover rounded"
                  />
                ))}
              </div>
            )}
            
            <div className="flex items-center justify-between">
              <Button
                variant="ghost"
                size="sm"
                onClick={() => markHelpful(review.id)}
                disabled={!currentUserId || (review.helpfulUsers && review.helpfulUsers.includes(currentUserId))}
                className="flex items-center gap-2"
              >
                <ThumbsUp className="h-4 w-4" />
                Helpful ({review.helpfulCount})
              </Button>
              
              {review.reply && (
                <div className="bg-gray-50 p-3 rounded-lg mt-3">
                  <div className="flex items-center gap-2 mb-1">
                    <MessageSquare className="h-4 w-4" />
                    <span className="font-medium text-sm">Business Response</span>
                    <span className="text-xs text-gray-600">
                      {new Date(review.replyDate!).toLocaleDateString()}
                    </span>
                  </div>
                  <p className="text-sm">{review.reply}</p>
                </div>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default ReviewSystem;