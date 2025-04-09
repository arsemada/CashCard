package com.example.CashCard.controller;

import com.example.CashCard.entity.CashCard;
import com.example.CashCard.repository.CashCardRepository;
import com.example.CashCard.controller.RequestMapping;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/cashcards")
public class CashCardWebController {

    private final CashCardRepository cashCardRepository;
    private final List<String> cardColors = List.of("#a8dadc", "#457b9d", "#1d3557", "#f1faee", "#e63946", "#a1887f"); // Updated pastel-like colors

    public CashCardWebController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String viewAllCashCardsHtml() {
        List<CashCard> cashCards = cashCardRepository.findAll();
        StringBuilder html = new StringBuilder("<!DOCTYPE html>");
        html.append("<html lang='en'><head><meta charset='UTF-8'>");
        html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        html.append("<title>Family Cash Cards</title>");
        html.append("<link rel='stylesheet' href='/css/style.css'></head><body>");
        html.append("<div class='app-container'>");
        html.append("<header class='app-header'>");
        html.append("<h1 class='app-title'>Family Cash Cards</h1>");
        html.append("<a href='/cashcards/add-form' class='button primary'>+ Add New Member</a>");
        html.append("</header>");
        html.append("<main class='card-list'>");
        if (cashCards.isEmpty()) {
            html.append("<p class='empty-message'>No family members added yet.</p>");
        } else {
            for (int i = 0; i < cashCards.size(); i++) {
                CashCard card = cashCards.get(i);
                String cardColor = cardColors.get(i % cardColors.size());
                html.append("<div class='card'>");
                html.append("<div class='card-content' style='background-color: ").append(cardColor).append(";'>");
                html.append("<h2 class='member-name'>").append(card.getKidName()).append("</h2>");
                html.append("<div class='card-details'>");
                html.append("<span class='detail-label'>Balance:</span>");
                html.append("<span class='balance'>$").append(String.format("%.2f", card.getBalance())).append("</span>");
                html.append("</div>");
                html.append("<div class='card-actions'>");
                html.append("<a href='/cashcards/edit-form/" + card.getId() + "' class='button secondary small'>Edit</a>");
                html.append("</div>");
                html.append("</div>");
                html.append("</div>");
            }
        }
        html.append("</main>");
        html.append("</div></body></html>");
        return html.toString();
    }

    @PostMapping("/process-member")
    @ResponseBody
    public String processMember(@RequestParam("memberName") String memberName,
                                @RequestParam("cardAmount") String cardAmount,
                                RedirectAttributes redirectAttributes) {
        System.out.println("Received memberName: " + memberName);
        System.out.println("Received cardAmount: " + cardAmount);

        CashCard newCashCard = new CashCard();
        newCashCard.setKidName(memberName);
        try {
            BigDecimal balance = new BigDecimal(cardAmount);
            newCashCard.setBalance(balance);
            cashCardRepository.save(newCashCard);
            return "<!DOCTYPE html><html><head><title>Member Added</title><meta http-equiv='refresh' content='2;url=/cashcards'></head><body><div class='notification success'>Member and Cash Added! Redirecting...</div></body></html>";
        } catch (NumberFormatException e) {
            return "<!DOCTYPE html><html><head><title>Input Error</title></head><body><div class='notification error'>Error: Invalid amount '" + cardAmount + "' for member '" + memberName + "'. Please enter a valid number. <a href='/cashcards/add-form'>Try Again</a></div></body></html>";
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String findCashCardHtml(@PathVariable Long id) {
        Optional<CashCard> cashCardOptional = cashCardRepository.findById(id);
        if (cashCardOptional.isPresent()) {
            CashCard card = cashCardOptional.get();
            return "<!DOCTYPE html><html><head><title>" + card.getKidName() + "'s Card</title><link rel='stylesheet' href='/css/style.css'></head><body><div class='details-container'><div class='card details-card' style='background-color: " + cardColors.get((int) (card.getId() % cardColors.size())) + ";'><h2 class='member-name'>" + card.getKidName() + "</h2><div class='card-details'><span class='detail-label'>Balance:</span> <span class='balance'>$" + String.format("%.2f", card.getBalance()) + "</span></div><p><a href='/cashcards' class='button secondary small'>Back to Family Cards</a></p></div></div></body></html>";
        } else {
            return "<!DOCTYPE html><html><head><title>Cash Card Not Found</title></head><body><div class='notification error'><h1>Cash Card Not Found</h1><p><a href='/cashcards' class='button secondary small'>Back to Family Cards</a></p></div></body></html>";
        }
    }

    @GetMapping("/add-form")
    @ResponseBody
    public String addMemberForm() {
        StringBuilder html = new StringBuilder("<!DOCTYPE html>");
        html.append("<html lang='en'><head><meta charset='UTF-8'>");
        html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        html.append("<title>Add New Member</title>");
        html.append("<link rel='stylesheet' href='/css/style.css'></head><body>");
        html.append("<div class='form-container'>");
        html.append("<header class='form-header'>");
        html.append("<a href='/cashcards' class='back-link'>&larr; Back</a>");
        html.append("<h1 class='form-title'>Add New Family Member</h1>");
        html.append("</header>");
        html.append("<form action='/cashcards/add' method='post' class='member-form'>");
        html.append("<div class='form-group'>");
        html.append("<label for='kidName'>Name:</label>");
        html.append("<input type='text' id='kidName' name='kidName' class='form-control' required/>");
        html.append("</div>");
        html.append("<div class='form-group'>");
        html.append("<label for='balance'>Initial Balance:</label>");
        html.append("<div class='input-group'>");
        html.append("<span class='input-group-addon'>$</span>");
        html.append("<input type='number' id='balance' name='balance' step='0.01' value='0.00' class='form-control' required/>");
        html.append("</div>");
        html.append("</div>");
        html.append("<div class='form-group'>");
        html.append("<label for='parentName'>Parent Name (Optional):</label>");
        html.append("<input type='text' id='parentName' name='parentName' class='form-control'/>");
        html.append("</div>");
        html.append("<div class='form-actions'>");
        html.append("<button type='submit' class='button primary'>Add Member</button>");
        html.append("<a href='/cashcards' class='button secondary'>Cancel</a>");
        html.append("</div>");
        html.append("</form>");
        html.append("</div></body></html>");
        return html.toString();
    }

    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String addFamilyMember(CashCard newCashCard) {
        System.out.println("Received newCashCard: " + newCashCard);
        cashCardRepository.save(newCashCard);
        return "<!DOCTYPE html><html><head><title>Member Added</title><meta http-equiv='refresh' content='2;url=/cashcards'></head><body><div class='notification success'>Member Added Successfully! Redirecting...</div></body></html>";
    }

    @GetMapping(value = "/edit-form/{id}", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String showEditForm(@PathVariable Long id) {
        Optional<CashCard> cashCardOptional = cashCardRepository.findById(id);
        if (cashCardOptional.isPresent()) {
            CashCard card = cashCardOptional.get();
            StringBuilder html = new StringBuilder("<!DOCTYPE html>");
            html.append("<html lang='en'><head><meta charset='UTF-8'>");
            html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            html.append("<title>Edit Member: ").append(card.getKidName()).append("</title>");
            html.append("<link rel='stylesheet' href='/css/style.css'></head><body>");
            html.append("<div class='form-container edit-form-container'>");
            html.append("<header class='form-header'>");
            html.append("<a href='/cashcards' class='back-link'>&larr; Back</a>");
            html.append("<h1 class='form-title'>Edit Member: ").append(card.getKidName()).append("</h1>");
            html.append("</header>");
            html.append("<form action='/cashcards/update/" + card.getId() + "' method='post' class='member-form'>");
            html.append("<div class='form-group'>");
            html.append("<label for='kidName'>Name:</label>");
            html.append("<input type='text' id='kidName' name='kidName' class='form-control' value='").append(card.getKidName()).append("' required/>");
            html.append("</div>");
            html.append("<div class='form-group'>");
            html.append("<label for='balance'>Balance:</label>");
            html.append("<div class='input-group'>");
            html.append("<span class='input-group-addon'>$</span>");
            html.append("<input type='number' id='balance' name='balance' step='0.01' class='form-control' value='").append(String.format("%.2f", card.getBalance())).append("' required/>");
            html.append("</div>");
            html.append("</div>");
            html.append("<div class='form-group'>");
            html.append("<label for='parentName'>Parent Name (Optional):</label>");
            html.append("<input type='text' id='parentName' name='parentName' class='form-control' value='").append(card.getParentName() == null ? "" : card.getParentName()).append("'/>");
            html.append("</div>");
            html.append("<div class='form-actions'>");
            html.append("<button type='submit' class='button primary'>Save Changes</button>");
            html.append("<a href='/cashcards' class='button secondary'>Cancel</a>");
            html.append("<a href='/cashcards/delete/" + card.getId() + "' class='button danger'>Delete Member</a>");
            html.append("</div>");
            html.append("</form>");
            html.append("</div></body></html>");
            return html.toString();
        } else {
            return "<!DOCTYPE html><html><head><title>Cash Card Not Found</title></head><body><div class='notification error'><h1>Cash Card Not Found</h1><p><a href='/cashcards' class='button secondary small'>Back to Family Cards</a></p></div></body></html>";
        }
    }

    @PostMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String updateCashCard(@PathVariable Long id, CashCard updatedCashCard) {
        Optional<CashCard> existingCashCardOptional = cashCardRepository.findById(id);
        if (existingCashCardOptional.isPresent()) {
            CashCard existingCard = existingCashCardOptional.get();
            existingCard.setKidName(updatedCashCard.getKidName());
            existingCard.setBalance(updatedCashCard.getBalance());
            existingCard.setParentName(updatedCashCard.getParentName());
            cashCardRepository.save(existingCard);
            return "<!DOCTYPE html><html><head><title>Member Updated</title><meta http-equiv='refresh' content='2;url=/cashcards'></head><body><div class='notification success'>Member Updated Successfully! Redirecting...</div></body></html>";
        } else {
            return "<!DOCTYPE html><html><head><title>Cash Card Not Found</title></head><body><div class='notification error'><h1>Cash Card Not Found</h1><p><a href='/cashcards' class='button secondary small'>Back to Family Cards</a></p></div></body></html>";
        }
    }

    @GetMapping("/delete/{id}")
    @ResponseBody
    public String deleteCashCard(@PathVariable Long id) {
        if (cashCardRepository.existsById(id)) {
            cashCardRepository.deleteById(id);
            return "<!DOCTYPE html><html><head><title>Member Deleted</title><meta http-equiv='refresh' content='2;url=/cashcards'></head><body><div class='notification success'>Member Deleted Successfully! Redirecting...</div></body></html>";
        } else {
            return "<!DOCTYPE html><html><head><title>Cash Card Not Found</title></head><body><div class='notification error'><h1>Cash Card Not Found</h1><p><a href='/cashcards'>Back to Family Cards</a></p></div></body></html>";
        }
    }
}