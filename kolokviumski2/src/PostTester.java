import java.util.*;

class Comment {
    String username;
    String commentId;
    String content;
    int likes;
    Map<String, Comment> replies;

    public Comment(String username, String commentId, String content) {
        this.username = username;
        this.commentId = commentId;
        this.content = content;
        this.likes = 0;
        this.replies = new HashMap<>();
    }


    public int totalLikes(){
        int totalLikes = likes;
        for (Comment reply : replies.values()) {
            totalLikes += reply.totalLikes();
        }
        return totalLikes;
    }

    public void addReply(Comment comment) {
        replies.putIfAbsent(comment.commentId, comment);
    }

    public boolean addNestedReply(String replyToId, Comment comment) {
        if (replies.containsKey(replyToId)) {
            replies.get(replyToId).addReply(comment);
            return true;
        }for (Comment nestedReply : replies.values()) {
            if (nestedReply.addNestedReply(replyToId, comment)) {
                return true;
            }
        }
        return false;
    }

    public void like(){
        likes++;
    }

    public boolean likeNestedComment(String commentId) {
        if (replies.containsKey(commentId))
            replies.get(commentId).like();
        else
            for (Comment nestedComment : replies.values())
                if (nestedComment.likeNestedComment(commentId))
                    return true;
        return false;
    }

    public String toString(String indent) {
//        Comment: Television into daughter high have mouth house. Word another official cup thank.
//                Written by: user1
//        Likes: 5
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append("Comment: ").append(content).append("\n");
        sb.append(indent).append("Written by: ").append(username).append("\n");
        sb.append(indent).append("Likes: ").append(likes).append("\n");
        //sb.append(indent).append("TotalLikes: ").append(totalLikes()).append("\n");
//        List<Comment> comments = new ArrayList<>(replies.values());
//        comments.sort(Comparator.comparingInt(Comment::getLikes).reversed());
//        for (Comment c : comments) {
//            sb.append(reply.toString(indent + "    "));
//        }
        replies.values().stream()
                //.sorted(Comparator.comparingInt(Comment::getLikes).reversed())
                .map(c -> c.toString(indent + "    ")).forEach(sb::append);

        return sb.toString();
    }

    public String getId() {
        return commentId;
    }
}

class Post {
    String postAuthor;
    String postContent;
    Map<String, Comment> comments;

    public Post(String postAuthor, String postContent) {
        this.postAuthor = postAuthor;
        this.postContent = postContent;
        this.comments = new HashMap<>();
    }

    public void addComment(String username, String commentId, String content, String replyToId) {
        Comment comment = new Comment(username, commentId, content);
        if (replyToId == null) {
            comments.putIfAbsent(commentId, comment);
        } else {
            Comment parentComment = comments.get(replyToId);
            if (parentComment != null) {
                parentComment.addReply(comment);
            } else {
                for (Comment c : comments.values())
                    if (c.addNestedReply(replyToId, comment))
                        return;
            }
        }
    }

    public void likeComment(String commentId) {
        if (comments.containsKey(commentId)){
            comments.get(commentId).like();
        }else {
            for (Comment c : comments.values()){
                if (c.likeNestedComment(commentId))
                    return;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Post: ").append(postContent).append("\n");
        sb.append("Written by: ").append(postAuthor).append("\n");
        sb.append("Comments:\n");
        comments.values().stream()
                .sorted(Comparator.comparingInt(Comment::totalLikes).reversed().thenComparing(Comment::getId))
                .map(c -> c.toString("        "))
                .forEach(sb::append);
        return sb.toString();
    }
}


public class PostTester {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String postAuthor = sc.nextLine();
        String postContent = sc.nextLine();

        Post p = new Post(postAuthor, postContent);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split(";");
            String testCase = parts[0];

            if (testCase.equals("addComment")) {
                String author = parts[1];
                String id = parts[2];
                String content = parts[3];
                String replyToId = null;
                if (parts.length == 5) {
                    replyToId = parts[4];
                }
                p.addComment(author, id, content, replyToId);
            } else if (testCase.equals("likes")) { //likes;1;2;3;4;1;1;1;1;1 example
                for (int i = 1; i < parts.length; i++) {
                    p.likeComment(parts[i]);
                }
            } else {
                System.out.println(p);
            }

        }
    }
}
