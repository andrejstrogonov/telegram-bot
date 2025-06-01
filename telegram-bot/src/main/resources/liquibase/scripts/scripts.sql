-- Create index on id for faster access
CREATE INDEX idx_notifications_id ON notifications (id);

-- Create index on chat_id for faster searches
CREATE INDEX idx_notifications_chat_id ON notifications (chat_id);

-- Create index on notification_text for faster text searches
CREATE INDEX idx_notifications_notification_text ON notifications (notification_text);

-- Create index on send_datetime for faster datetime queries
CREATE INDEX idx_notifications_send_datetime ON notifications (send_datetime);