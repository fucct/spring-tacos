package tacos.web;

import javax.validation.Valid;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.Errors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.support.SessionStatus;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tacos.Order;
import tacos.data.OrderRepository;
import tacos.User;

@Slf4j
@Controller
@RequestMapping("/orders")
@SessionAttributes("order")
@RequiredArgsConstructor
public class OrderController {

	private final OrderRepository orderRepo;

	private final OrderProps props;

	@GetMapping
	public String ordersForUser(@AuthenticationPrincipal User user, Model model) {
		Pageable pageable = PageRequest.of(0, props.getPageSize());
		model.addAttribute("orders", orderRepo.findByUserOrderByPlacedAtDesc(user, pageable));

		return "orderList";
	}

	@GetMapping("/current")
	  public String orderForm(@AuthenticationPrincipal User user,
			  				  @ModelAttribute Order order) {
		  if (order.getDeliveryName() == null) {
			  order.setDeliveryName(user.getFullname());
		  }
		  if (order.getDeliveryStreet() == null) {
			  order.setDeliveryStreet(user.getStreet());
		  }
		  if (order.getDeliveryCity() == null) {
			  order.setDeliveryCity(user.getCity());
		  }
		  if (order.getDeliveryState() == null) {
			  order.setDeliveryState(user.getState());
	      }
		  if (order.getDeliveryZip() == null) {
			  order.setDeliveryZip(user.getZip());
		  }

	    return "orderForm";
	  }

	@PostMapping
	  public String processOrder(@Valid Order order, 
			  Errors errors, SessionStatus sessionStatus
			  , @AuthenticationPrincipal User user) {
		if (errors.hasErrors()) {
		    return "orderForm";
		}

		order.setUser(user); 
		
		orderRepo.save(order);
	    sessionStatus.setComplete();

	    return "redirect:/";
	  }

}
