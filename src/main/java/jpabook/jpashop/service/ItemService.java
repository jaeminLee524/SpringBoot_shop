package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) //조회성
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    /**
     * item 저장
     */
    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    /**
     * 다중 item 조회
     */
    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    /**
     * 단건 item 조회
     */
    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }

    /**
     * 영속성 컨텍스트가 자동 변경
     */
    @Transactional
    public void updateItem(Long id, String name, int price) {
        //영속성 데이터를 찾아옴
        Item item = itemRepository.findOne(id);

        //영속성 데이터에 셋팅
        item.setName(name);
        item.setPrice(price);
    }
}
