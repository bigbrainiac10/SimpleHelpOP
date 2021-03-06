package com.bigbrainiac10.simplehelpop.viewmenu;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import com.bigbrainiac10.simplehelpop.HelpQuestion;
import com.bigbrainiac10.simplehelpop.SHOConfigManager;
import com.bigbrainiac10.simplehelpop.SimpleHelpOp;
import com.bigbrainiac10.simplehelpop.Utility;
import com.bigbrainiac10.simplehelpop.events.QuestionAnsweredEvent;

public class ReplyQuestionConversation extends StringPrompt{

	private SimpleHelpOp plugin = SimpleHelpOp.getInstance();
	
	private final String replyAdded = Utility.safeToColor(SHOConfigManager.getPlayerMessage("replyAdded"));
	private final String replyStart = Utility.safeToColor(SHOConfigManager.getPlayerMessage("replyStart"));
	
	private HelpQuestion question;
	private UUID player;
	
	public ReplyQuestionConversation(HelpQuestion question, Player player){
		this.question = question;
		this.player = player.getUniqueId();
	}
	
	@Override
	public Prompt acceptInput(ConversationContext con, String answer) {
		Timestamp time = new Timestamp(Calendar.getInstance().getTimeInMillis());
		
		question.replyTime = time;
		question.reply = answer;
		question.replier_uuid = player.toString();
		
		try {
			plugin.getHelpOPData().updateQuestion(question);
			QuestionAnsweredEvent qe = new QuestionAnsweredEvent(question);
			Bukkit.getServer().getPluginManager().callEvent(qe);
			con.getForWhom().sendRawMessage(replyAdded);
		} catch (SQLException e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to update question.", e);
		}
		
		return Prompt.END_OF_CONVERSATION;
	}

	@Override
	public String getPromptText(ConversationContext con) {
		OfflinePlayer op = Bukkit.getOfflinePlayer(player);
		return replyStart.replace("%QUESTION%", question.getQuestion())
				.replace("%PLAYER%", op != null ? op.getName() : player.toString());
	}

}
