package com.macinternetservices.sablebusinessdirectory.ui.comment.list.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.macinternetservices.sablebusinessdirectory.R;
import com.macinternetservices.sablebusinessdirectory.databinding.ItemCommentListAdapterBinding;
import com.macinternetservices.sablebusinessdirectory.ui.common.DataBoundListAdapter;
import com.macinternetservices.sablebusinessdirectory.ui.common.DataBoundViewHolder;
import com.macinternetservices.sablebusinessdirectory.utils.Constants;
import com.macinternetservices.sablebusinessdirectory.utils.Objects;
import com.macinternetservices.sablebusinessdirectory.viewobject.Comment;

import androidx.databinding.DataBindingUtil;

/**
 * Sable Business Directory
 * Contact Email : admin@sablebusinessdirectory.com
 * Website : http://www.sablebusinessdirectory.com
 */

public class CommentListAdapter extends DataBoundListAdapter<Comment, ItemCommentListAdapterBinding> {
    private final androidx.databinding.DataBindingComponent dataBindingComponent;
    private CommentClickCallback callback;
    private DataBoundListAdapter.DiffUtilDispatchedInterface diffUtilDispatchedInterface;

    public CommentListAdapter(androidx.databinding.DataBindingComponent dataBindingComponent,
                              CommentClickCallback callback,
                              DiffUtilDispatchedInterface diffUtilDispatchedInterface) {
        this.dataBindingComponent = dataBindingComponent;
        this.callback = callback;
        this.diffUtilDispatchedInterface = diffUtilDispatchedInterface;
    }

    @Override
    protected ItemCommentListAdapterBinding createBinding(ViewGroup parent) {
        ItemCommentListAdapterBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.item_comment_list_adapter, parent, false,
                        dataBindingComponent);
        binding.getRoot().setOnClickListener(v -> {
            Comment comment = binding.getComment();
            if (comment != null && callback != null) {
                callback.onClick(comment);
            }
        });
        return binding;
    }

    @Override
    public void bindView(DataBoundViewHolder<ItemCommentListAdapterBinding> holder, int position) {
        super.bindView(holder, position);

    }

    @Override
    protected void dispatched() {
        if (diffUtilDispatchedInterface != null) {
            diffUtilDispatchedInterface.onDispatched();
        }
    }

    @Override
    protected void bind(ItemCommentListAdapterBinding binding, Comment item) {
        binding.setComment(item);
        if (item.commentReplyCount.equals(Constants.ZERO)) {
            binding.replyCountTextView.setVisibility(View.GONE);
        } else {
            binding.replyCountTextView.setVisibility(View.VISIBLE);
        }

        binding.replyCountTextView.setText(binding.getRoot().getResources().getString(R.string.comment__replies, item.commentReplyCount));

    }

    @Override
    protected boolean areItemsTheSame(Comment oldItem, Comment newItem) {
        return Objects.equals(oldItem.id, newItem.id)
                && oldItem.headerComment.equals(newItem.headerComment)
                && oldItem.commentReplyCount.equals(newItem.commentReplyCount);
    }

    @Override
    protected boolean areContentsTheSame(Comment oldItem, Comment newItem) {
        return Objects.equals(oldItem.id, newItem.id)
                && oldItem.headerComment.equals(newItem.headerComment)
                && oldItem.commentReplyCount.equals(newItem.commentReplyCount);
    }

    public interface CommentClickCallback {
        void onClick(Comment comment);
    }
}
