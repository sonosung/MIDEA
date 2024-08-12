package org.astrologist.midea.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.astrologist.midea.entity.MindlistAdmin;
import org.astrologist.midea.entity.QMindlistAdmin;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
public class MindlistRepositoryTests {

    @Autowired
    private MindlistAdminRepository mindlistAdminRepository;

    @Test
    public void insertDummies() {
        IntStream.rangeClosed(1,5).forEach(i-> {

            MindlistAdmin mindlistAdmin = MindlistAdmin.builder()
                    .composer("composer"+i)
                    .title("Title...." + i)
                    .url("usl..." +i)
                    .content("content.."+i)
                    .writer("user" + (i % 10))
                    .build();
            System.out.println(mindlistAdminRepository.save(mindlistAdmin));
        });
    }

    @Test
    public void updateTest() {

        Optional<MindlistAdmin> result = mindlistAdminRepository.findById(20L);

        if(result.isPresent()){

            MindlistAdmin mindlist = result.get();

            mindlist.changeComposer("Composer has changed..");
            mindlist.changeUrl("URL has changed..");
            mindlist.changeTitle("Title has changed..");
            mindlist.changeContent(("content has changed.."));

            mindlistAdminRepository.save(mindlist);

        }
    }

    @Test
    public void testQuery1(){

        Pageable pageable = PageRequest.of(0, 10, Sort.by("mno").descending());

        QMindlistAdmin qMindlistAdmin = QMindlistAdmin.mindlistAdmin; //1

        String keyword = "1";

        BooleanBuilder builder = new BooleanBuilder(); //2

        BooleanExpression expression = qMindlistAdmin.composer.contains(keyword); //3

        builder.and(expression); //4

        Page<MindlistAdmin> result = mindlistAdminRepository.findAll(builder, pageable); //5

        result.stream().forEach((mindlistAdmin -> {
            System.out.println(mindlistAdmin);
        }));
    }

    @Test
    public void testQuery2(){

        Pageable pageable = PageRequest.of(0, 10, Sort.by("mno").descending());

        QMindlistAdmin qMindlistAdmin = QMindlistAdmin.mindlistAdmin;

        String keyword = "1";

        BooleanBuilder builder = new BooleanBuilder();

        BooleanExpression exTitle = qMindlistAdmin.composer.contains(keyword); //제목에 포함된 키워드.

        BooleanExpression exContent = qMindlistAdmin.url.contains(keyword); //내용에 포함된 키워드.

        BooleanExpression exAll = exTitle.or(exContent); //타이틀과 내용 둘중에 하나라도 포함되있을 경우.

        builder.and(exAll);

        builder.and(qMindlistAdmin.mno.gt(5L)); // gt 는 greater than 연산자. mno가 250L 큰 숫자의 게시물이 조회됨.

        Page<MindlistAdmin> result = mindlistAdminRepository.findAll(builder, pageable); //5

        result.stream().forEach((mindlistAdmin -> {
            System.out.println(mindlistAdmin);
        }));
    }

}
